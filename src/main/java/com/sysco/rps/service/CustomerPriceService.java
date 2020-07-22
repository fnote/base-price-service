package com.sysco.rps.service;

import com.sysco.rps.common.Errors;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.Product;
import com.sysco.rps.exceptions.RefPriceAPIException;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
 * Contains logic on processing the price request by calling repositories
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 18. Jun 2020 16:00
 */

@Service
public class CustomerPriceService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPriceService.class);

    @Autowired
    private CustomerPriceRepository repository;

    @Autowired
    private BusinessUnitLoaderService businessUnitLoaderService;

    public Mono<CustomerPriceResponse> pricesByOpCo(CustomerPriceRequest request, Integer requestedSupcsPerQuery) {

        // TODO: See whether Mono.error can be achieved through Aspects or by some other means
        RefPriceAPIException validationException = validateRequest(request);

        if (validationException != null) {
            return Mono.error(validationException);
        }

        List<String> requestedSUPCs = request.getProducts().stream().distinct().collect(Collectors.toList());

        int supcsPerQuery = (requestedSupcsPerQuery == null) ? requestedSUPCs.size() : requestedSupcsPerQuery;

        List<List<String>> supcsPartitions = ListUtils.partition(requestedSUPCs, supcsPerQuery);

        AtomicLong timeConsumedForDbActivities = new AtomicLong(0);

        return Flux.fromIterable(supcsPartitions)
              .flatMap(supcPartition -> repository.getPricesByOpCo(request, supcPartition))
              .elapsed()
              .map(t -> {
                  timeConsumedForDbActivities.addAndGet(t.getT1());
                  return t.getT2();
              })
              .subscriberContext(Context.of(ROUTING_KEY, request.getBusinessUnitNumber()))
              .collectList()
              .flatMap(v -> {
                  Map<String, Product> productMap = new HashMap<>();
                  v.forEach(p -> {
                      String supc = p.getSupc();
                      Product existingProduct = productMap.get(supc);
                      if (existingProduct == null || (existingProduct.getPriceExportDate() < p.getPriceExportDate())) {
                          productMap.put(supc, p);
                      }
                  });
                  logger.info("TOTAL-DB-TIME : [{}]", timeConsumedForDbActivities.get());
                  return Mono.just(formResponse(request, requestedSUPCs, productMap));
              }).doOnError(e -> {
                  logger.error("Request Payload: [{}]", request);
                  logger.error(e.getMessage(), e);
              });

    }

    private RefPriceAPIException validateRequest(CustomerPriceRequest request) {
        // Validate the OpCo

        if (StringUtils.isEmpty(request.getBusinessUnitNumber())) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.REQUESTED_OPCO_NULL_OR_EMPTY, Errors.Messages.REQUESTED_OPCO_NULL_OR_EMPTY);
        }

        if (!businessUnitLoaderService.isOpcoExist(request.getBusinessUnitNumber())) {
            return new RefPriceAPIException(HttpStatus.NOT_FOUND, Errors.Codes.OPCO_NOT_FOUND, Errors.Messages.MSG_OPCO_NOT_FOUND);
        }

        if (StringUtils.isEmpty(request.getCustomerAccount())) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.CUSTOMER_NULL_OR_EMPTY, Errors.Messages.CUSTOMER_NULL_OR_EMPTY);
        }
        return null;
    }

    private CustomerPriceResponse formResponse(CustomerPriceRequest request, List<String> requestedSUPCs, Map<String, Product> foundProductsMap) {

        List<ErrorDTO> errors = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        String customer = request.getCustomerAccount();

        if (foundProductsMap.size() == requestedSUPCs.size()) {
            products.addAll(foundProductsMap.values());
        } else {

            for (String productId : requestedSUPCs) {

                Product product = foundProductsMap.get(productId);
                if (product == null) {
                    String errorData = String.format("Price not found for SUPC: %s Customer: %s", productId, customer);
                    errors.add(new ErrorDTO(Errors.Codes.MAPPING_NOT_FOUND, Errors.Messages.MAPPING_NOT_FOUND, errorData));
                } else {
                    products.add(product);
                }

            }
        }

        return new CustomerPriceResponse(request, products, errors);
    }
}
