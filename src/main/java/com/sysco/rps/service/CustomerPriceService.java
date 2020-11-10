package com.sysco.rps.service;

import com.sysco.rps.common.Errors;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.MetricsEvent;
import com.sysco.rps.dto.MinorErrorDTO;
import com.sysco.rps.dto.Product;
import com.sysco.rps.exceptions.RefPriceAPIException;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import com.sysco.rps.util.MetricsLoggerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static com.sysco.rps.common.Constants.DEFAULT_PRICE_ZONE;
import static com.sysco.rps.common.Constants.PRICE_REQUEST_DATE_PATTERN;
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

    private final Integer configuredSUPCsPerQuery;

    CustomerPriceService(@Value("${supcs.per.query:5}") Integer configuredSUPCsPerQuery) {
        this.configuredSUPCsPerQuery = configuredSUPCsPerQuery;
    }

    @Autowired
    private CustomerPriceRepository repository;

    @Autowired
    private BusinessUnitLoaderService businessUnitLoaderService;

    public Mono<CustomerPriceResponse> pricesByOpCo(CustomerPriceRequest request, Integer requestedSupcsPerQuery) {

        RefPriceAPIException validationException = validateRequest(request);

        if (validationException != null) {
            return Mono.error(validationException);
        }

        List<String> requestedSUPCs = request.getProducts().stream().distinct().collect(Collectors.toList());

        int supcsPerQuery = (requestedSupcsPerQuery == null || requestedSupcsPerQuery == 0) ? configuredSUPCsPerQuery : requestedSupcsPerQuery;

        List<List<String>> supcsPartitions = ListUtils.partition(requestedSUPCs, supcsPerQuery);

        AtomicLong timeConsumedForDbActivities = new AtomicLong(0);

        return Flux.fromIterable(supcsPartitions)
              .flatMap(supcPartition -> repository.getPricesByOpCo(request, supcPartition))
              .elapsed()
              .map(t -> {
                  timeConsumedForDbActivities.addAndGet(t.getT1());
                  return t.getT2();
              })
              .collectList()
              .flatMap(productList -> {
                  Map<String, Product> productMap = convertToUniqueProductMap(productList);
                  logger.info("TOTAL-DB-TIME : [{}]", timeConsumedForDbActivities.get());
                  return formResponseWithDefaultPriceProducts(request, requestedSUPCs, productMap);
              })
              .doOnSuccess(resp -> {
                  MetricsEvent metricsEvent = new MetricsEvent("customer-prices", request, resp, 0L, 0L, supcsPerQuery, null);
                  MetricsLoggerUtils.logInfo(metricsEvent);
              })
              .doOnError(e -> {
                  logger.error("Failed to fetch prices");
                  logger.error("Request Payload: [{}]", request);
                  logger.error(e.getMessage(), e);

                  MetricsEvent metricsEvent = new MetricsEvent("customer-prices", request, null, 0L, 0L, supcsPerQuery, null);
                  MetricsLoggerUtils.logError(metricsEvent);
              })
              .subscriberContext(Context.of(ROUTING_KEY, request.getBusinessUnitNumber()));
    }

    private RefPriceAPIException validateRequest(CustomerPriceRequest request) {
        // Validate the OpCo

        if (StringUtils.isEmpty(request.getBusinessUnitNumber())) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.REQUESTED_OPCO_NULL_OR_EMPTY, Errors.Messages.REQUESTED_OPCO_NULL_OR_EMPTY);
        }

        if (!businessUnitLoaderService.isOpcoExist(request.getBusinessUnitNumber())) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.OPCO_NOT_FOUND, Errors.Messages.MSG_OPCO_NOT_FOUND);
        }

        if (StringUtils.isEmpty(request.getCustomerAccount())) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.CUSTOMER_NULL_OR_EMPTY, Errors.Messages.CUSTOMER_NULL_OR_EMPTY);
        }

        if (request.getProducts() == null) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.PRODUCTS_NOT_FOUND_IN_REQUEST, Errors.Messages.MSG_PRODUCTS_NOT_FOUND_IN_REQUEST);
        }

        if (!GenericValidator.isDate(request.getPriceRequestDate(), PRICE_REQUEST_DATE_PATTERN, true)) {
            return new RefPriceAPIException(HttpStatus.BAD_REQUEST, Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST,
                  Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST);
        }
        return null;
    }

    /**
     * When there are requested products with missing mappings, uses price zone 3's price as the default price
     */
    private Mono<CustomerPriceResponse> formResponseWithDefaultPriceProducts(CustomerPriceRequest request, List<String> requestedSUPCs,
                                                                             Map<String, Product> foundProductsMap) {

        List<MinorErrorDTO> errors = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        if (foundProductsMap.size() == requestedSUPCs.size()) {
            products.addAll(foundProductsMap.values());
            return Mono.just(new CustomerPriceResponse(request, products, errors));
        } else {
            ArrayList<String> supcsToFindDefaults = new ArrayList<>(requestedSUPCs);
            supcsToFindDefaults.removeAll(foundProductsMap.keySet());

            return getDefaultProducts(request, supcsToFindDefaults)
                  .flatMap(defaultProducts -> {

                      if (!CollectionUtils.isEmpty(defaultProducts.values())) {
                          logger.info("Using default prices for: {}", defaultProducts.values());
                          foundProductsMap.putAll(defaultProducts);
                      }

                      for (String productId : requestedSUPCs) {

                          Product product = foundProductsMap.get(productId);
                          if (product == null) {
                              errors.add(new MinorErrorDTO(productId, Errors.Codes.MAPPING_NOT_FOUND, Errors.Messages.MAPPING_NOT_FOUND));
                          } else {
                              products.add(product);
                          }
                      }
                      return Mono.just(new CustomerPriceResponse(request, products, errors));
                  });
        }
    }

    private Mono<Map<String, Product>> getDefaultProducts(CustomerPriceRequest request, List<String> requestedSUPCs) {

        return repository.getPricesForSpecificPriceZone(requestedSUPCs, request.getPriceRequestDate(), DEFAULT_PRICE_ZONE, true)
              .subscriberContext(Context.of(ROUTING_KEY, request.getBusinessUnitNumber()))
              .collectList()
              .flatMap(result -> Mono.just(convertToUniqueProductMap(result)))
              .doOnError(e -> {
                  logger.error("Failed to fetch default prices");
                  logger.error("Request Payload: [{}]", request);
                  logger.error(e.getMessage(), e);
              });
    }

    private Map<String, Product> convertToUniqueProductMap(List<Product> result) {
        Map<String, Product> productMap = new HashMap<>();

        result.forEach(defaultProduct -> {
            String supc = defaultProduct.getSupc();
            Product existingProduct = productMap.get(supc);
            if (existingProduct == null || (existingProduct.getPriceExportTimestamp() < defaultProduct.getPriceExportTimestamp())) {
                productMap.put(supc, defaultProduct);
            }
        });
        return productMap;
    }
}
