package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.repository.TestUtilsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Class for controller unit tests
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Labs
 * @doc
 * @end Created : 21. Jul 2020 15:03
 */
class CustomerPriceControllerTest extends BaseTest {

    @Autowired
    CustomerPriceController customerPriceController;

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
    void getCustomerPrices_PRCP_2078() {

        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest request = new CustomerPriceRequest("020", "100001", "2020-02-02", new ArrayList<>(Arrays.asList("2000000","2000001","2000002")));

        Mono<CustomerPriceResponse> customerPrices = customerPriceController.getCustomerPrices(request, null);

        StepVerifier.create(customerPrices)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(3, result.getFailedItems().size());

                  // TODO: Complete

              })
              .verifyComplete();

    }
}
