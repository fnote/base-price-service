package com.sysco.rps.service;

import com.sysco.rps.dto.CustomerPrice;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.Product;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
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

    public Mono<CustomerPrice> pricesByOpCo(CustomerPriceRequest request, Integer requestedSupcsPerQuery) {

        int supcsPerQuery = (requestedSupcsPerQuery == null) ? request.getProducts().size() : requestedSupcsPerQuery;

        List<List<String>> supcsPartitions = ListUtils.partition(request.getProducts(), supcsPerQuery);

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
                  return Mono.just(new CustomerPrice(request, new ArrayList<>(productMap.values())));
              })
              .doOnError(e -> {
                  logger.error("Request Payload: [{}]", request);
                  logger.error(e.getMessage(), e);
              });

    }
}
