package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.MinorErrorDTO;
import com.sysco.rps.exceptions.RefPriceAPIException;
import com.sysco.rps.repository.TestUtilsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
        boolean paBulkDataSuccess = testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        boolean eatsBulkDataSuccess = testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        if (!(paBulkDataSuccess && eatsBulkDataSuccess)) {
            throw new RefPriceAPIException(null, "", "Initial data load failed");
        }

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /***
     * Testing PRCP-2078
     */
    @Test
    @Tag("desc:PRCP-2078")
    void getCustomerPrices_PRCP_2078() {

        RefPriceAPIException opcoInvalidException = new RefPriceAPIException(HttpStatus.BAD_REQUEST, "102010", "Couldn't find a matching DB for the requested OpCo");
        RefPriceAPIException opCoEmptyException = new RefPriceAPIException(HttpStatus.BAD_REQUEST, "102040", "OpCo ID should not be null/empty");

        // with correct data
        CustomerPriceRequest request = new CustomerPriceRequest("020", "68579367", "20200210", new ArrayList<>(Arrays.asList("2512527", "3325677", "8328971")));

        Mono<CustomerPriceResponse> customerPrices = customerPriceController.getCustomerPrices(request, null);

        StepVerifier.create(customerPrices)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(3, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());
              })
              .verifyComplete();

        // null OpCo ID
        request.setBusinessUnitNumber(null);
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opCoEmptyException, customerPrices);


        // Empty OpCo ID
        request.setBusinessUnitNumber("");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opCoEmptyException, customerPrices);

        // OpCo ID: 00
        request.setBusinessUnitNumber("00");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opcoInvalidException, customerPrices);

        // OpCo ID: -21
        request.setBusinessUnitNumber("-21");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opcoInvalidException, customerPrices);

        // OpCo ID length 256 chars
        request.setBusinessUnitNumber("E1aijYo394mRFFqMozy81uG2oQpFTinipk46777u7bveSm8c8tKGZwiajgklPtsZKalqS29RZfNkkrWBxCKucokZrlJ2RtZdBriOoOn5AOaqzqJGbPO8A5kgh88YE9PSSq1GLsFxYC0MzFxiAwl78iFG2g7mhLwqCblXQ3UD2brRdKt8vroDjnu95y6bQLPgR8fTHye8lAnoKRMfrAp1W2nnsNyectJB0Lqs5CY04Mm5vwU9KIcB1Pvho3f6TtKR\n");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opcoInvalidException, customerPrices);

        // OpCo ID: 092 (correct opco, but does not exist)
        request.setBusinessUnitNumber("092");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opcoInvalidException, customerPrices);

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

    /***
     * Testing PRCP-2079
     */
    @Test
    @Tag("desc:PRCP-2079")
    void getCustomerPrices_PRCP_2079_invalid_customer() {

        RefPriceAPIException opCoEmptyException = new RefPriceAPIException(HttpStatus.BAD_REQUEST, "102030", "Customer ID should not be null/empty");

        // with correct data
        CustomerPriceRequest request = new CustomerPriceRequest("020", "68579367", "20200210", new ArrayList<>(Arrays.asList("2512527", "3325677",
              "8328971")));

        Mono<CustomerPriceResponse> customerPrices;

        // null Customer ID
        request.setCustomerAccount(null);
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opCoEmptyException, customerPrices);


        // Empty Customer ID
        request.setCustomerAccount("");
        customerPrices = customerPriceController.getCustomerPrices(request, null);
        verifyInvalidOpCoError(opCoEmptyException, customerPrices);

        // Customer ID: 00
        request.setCustomerAccount("00");
        customerPrices = customerPriceController.getCustomerPrices(request, null);

        validateInvalidCustomer(customerPrices, "00");

        // Customer ID: -12345
        request.setCustomerAccount("-12345");
        customerPrices = customerPriceController.getCustomerPrices(request, null);

        validateInvalidCustomer(customerPrices, "-12345");

        // Customer ID length 256 chars
        String longCustomerId =
              "F1aijYo394mRFFqMozy81uG2oQpFTinipk46777u7bveSm8c8tKGZwiajgklPtsZKalqS29RZfNkkrWBxCKucokZrlJ2RtZdBriOoOn5AOaqzqJGbPO8A5kgh88YE9PSSq1GLs" +
                    "FxYC0MzFxiAwl78iFG2g7mhLwqCblXQ3UD2brRdKt8vroDjnu95y6bQLPgR8fTHye8lAnoKRMfrAp1W2nnsNyectJB0Lqs5CY04Mm5vwU9KIcB1Pvho3f6TtKR";
        request.setCustomerAccount(longCustomerId);
        customerPrices = customerPriceController.getCustomerPrices(request, null);

        validateInvalidCustomer(customerPrices, longCustomerId);

        // Customer ID; 400001 (correct Customer, but does not exist)
        request.setCustomerAccount("400001");
        customerPrices = customerPriceController.getCustomerPrices(request, null);

        validateInvalidCustomer(customerPrices, "400001");

    }

    private void validateInvalidCustomer(Mono<CustomerPriceResponse> customerPrices, String s) {
        StepVerifier.create(customerPrices)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(3, result.getFailedProducts().size());

                  String errorMsg = "Price not found for given SUPC/customer combination";

                  assertEquals(new MinorErrorDTO("2512527", "102020", errorMsg), result.getFailedProducts().get(0));
                  assertEquals(new MinorErrorDTO("3325677", "102020", errorMsg), result.getFailedProducts().get(1));
                  assertEquals(new MinorErrorDTO("8328971", "102020", errorMsg), result.getFailedProducts().get(2));
              })
              .verifyComplete();
    }
}
