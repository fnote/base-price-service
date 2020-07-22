package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.exceptions.RefPriceAPIException;
import com.sysco.rps.repository.TestUtilsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class for controller unit tests
 *
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

    @BeforeAll
    void initialSetup() {
        testUtilsRepository.truncateTables();
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCustomerPrices_PRCP_2078() {

        RefPriceAPIException expected = new RefPriceAPIException(HttpStatus.NOT_FOUND, "102010", "Couldn't find a matching DB for the requested OpCo");

        // with correct data
        CustomerPriceRequest request = new CustomerPriceRequest("020", "68579367", "2020-02-10", new ArrayList<>(Arrays.asList("2512527", "3325677",
              "8328971")));

        Mono<CustomerPriceResponse> customerPrices = customerPriceController.getCustomerPrices(request, null);

        StepVerifier.create(customerPrices)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(3, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());
              })
              .verifyComplete();

        // null OpCo ID
        request.setBusinessUnitNumber(null);
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(expected, customerPrices);


        // Empty OpCo ID
        request.setBusinessUnitNumber("");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(expected, customerPrices);

        // OpCo ID: 00
        request.setBusinessUnitNumber("00");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(expected, customerPrices);

        // OpCo ID: -21
        request.setBusinessUnitNumber("-21");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(expected, customerPrices);

        // OpCo ID length 256 chars
        request.setBusinessUnitNumber("E1aijYo394mRFFqMozy81uG2oQpFTinipk46777u7bveSm8c8tKGZwiajgklPtsZKalqS29RZfNkkrWBxCKucokZrlJ2RtZdBriOoOn5AOaqzqJGbPO8A5kgh88YE9PSSq1GLsFxYC0MzFxiAwl78iFG2g7mhLwqCblXQ3UD2brRdKt8vroDjnu95y6bQLPgR8fTHye8lAnoKRMfrAp1W2nnsNyectJB0Lqs5CY04Mm5vwU9KIcB1Pvho3f6TtKR\n");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(expected, customerPrices);

    }

    private void verifyInvalidOpCoError(RefPriceAPIException expected, Mono<CustomerPriceResponse> customerPrices) {
        StepVerifier.create(customerPrices)
              .expectErrorSatisfies(throwable -> {
                  assertNotNull(throwable);
                  assertTrue(throwable instanceof RefPriceAPIException, "Throwable not an instance of RefPriceAPIException");
                  RefPriceAPIException exception = (RefPriceAPIException) throwable;
                  assertEquals(expected, exception);
              })
              .verify();
    }
}
