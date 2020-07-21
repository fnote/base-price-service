package com.sysco.rps.service;

import com.sysco.rps.BaseTest;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.Product;
import com.sysco.rps.entity.PAData;
import com.sysco.rps.entity.PriceZoneData;
import com.sysco.rps.repository.TestUtilsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 20. Jul 2020 10:16
 */
class CustomerPriceServiceTest extends BaseTest {

    @Autowired
    CustomerPriceService customerPriceService;

    @Autowired
    TestUtilsRepository testUtilsRepository;

    @BeforeEach
    void setUp() {
        testUtilsRepository.truncateTables();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void pricesByOpCo() {
        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000002", "1000003", "1000004", "1000005"));

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2024-12-12", products);

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(result.getSuccessfulItems().size(), 0);
                  assertEquals(result.getFailedItems().size(), 5);

                  ErrorDTO errorDTO = result.getFailedItems().get(0);
                  assertEquals("102020", errorDTO.getCode());
                  assertEquals("Price not found for given SUPC/customer combination", errorDTO.getMessage());
                  assertEquals("Price not found for SUPC: 1000001 Customer: 100001", errorDTO.getErrorData());

              })
              .verifyComplete();
    }

    @Test
    void testRequestingDuplicateProducts() {

        testUtilsRepository.addPARecord(new PAData("1000001", 6, 100.20, "2024-12-12", 1595229000, 'C'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 6, "100001", "2024-12-12"));

        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000001", "1000002", "1000002"));
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2024-12-12", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

              })
              .verifyComplete();
    }

    // Testing query edge cases
    @Test
    void testSingleEffDateWithMultipleExportedDates() {
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "2021-03-01", 1495308212, 'C'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "2021-03-01", 1595308212, 'C'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "2021-02-02"));

        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2024-04-01", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  Product product = new Product("1000001", 3, 200.24, "2021-03-01 00:00:00", 1595308212L, 'C');
                  assertEquals(product, result.getSuccessfulItems().get(0));

              })
              .verifyComplete();
    }
}
