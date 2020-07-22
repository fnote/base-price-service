package com.sysco.rps.service;

import com.sysco.rps.BaseTest;
import com.sysco.rps.common.Errors;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.Product;
import com.sysco.rps.entity.PAData;
import com.sysco.rps.entity.PriceZoneData;
import com.sysco.rps.exceptions.RefPriceAPIException;
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

    private void validateFirstSuccessItem(CustomerPriceResponse result, String supc, Integer priceZoneId, Double referencePrice,
                                          String effectiveFromDate, Long priceExportDate, Character splitIndicator) {
        Product product = new Product(supc, priceZoneId, referencePrice, effectiveFromDate, priceExportDate, splitIndicator);
        assertEquals(product, result.getSuccessfulItems().get(0));
    }

    private void validateFirstMinorError(CustomerPriceResponse result, String code, String message, Object errorData) {
        ErrorDTO error = new ErrorDTO(code, message, errorData);
        assertEquals(result.getFailedItems().get(0), error);
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

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-01 00:00:00", 1595308212L, 'C');

              })
              .verifyComplete();
    }

    /***
     * Having Multiple effective dates with a single exported date
     */
    @Test
    void testMultipleEffDatesWithSingleExportedDates() {
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "2021-03-06", 1595308212, 'C'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "2021-03-10", 1595308212, 'C'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "2021-02-02"));

        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2020-03-05", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");

              })
              .verifyComplete();

        // date = min eff Date
        customerPriceRequest.setPriceRequestDate("2021-03-06");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-06 00:00:00", 1595308212L, 'C');

              })
              .verifyComplete();

        // date at the middle
        customerPriceRequest.setPriceRequestDate("2021-03-07");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-06 00:00:00", 1595308212L, 'C');

              })
              .verifyComplete();

        // date = last eff date
        customerPriceRequest.setPriceRequestDate("2021-03-10");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 125.24, "2021-03-10 00:00:00", 1595308212L, 'C');

              })
              .verifyComplete();

        // date after the last eff date
        customerPriceRequest.setPriceRequestDate("2021-03-12");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 125.24, "2021-03-10 00:00:00", 1595308212L, 'C');

              })
              .verifyComplete();
    }

    /***
     * Having max(effective_date) < max (exported_date)
     *
     */
    @Test
    void testMaxEffDateLessThanMaxExportedDate() {

        // Exported date: 2021-03-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "2021-04-10", 1616578866, 'C'));
        // Exported date: 2021-06-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "2021-04-10", 1624527666, 'C'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "2021-02-02"));


        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2020-03-05", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");

              })
              .verifyComplete();

        // date > min exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("2021-03-20");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");
              })
              .verifyComplete();

        // date > min exported date && > min eff date
        customerPriceRequest.setPriceRequestDate("2021-04-11");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-04-10 00:00:00", 1624527666L, 'C');

              })
              .verifyComplete();

        // date > max exported date && > max eff date
        customerPriceRequest.setPriceRequestDate("2021-06-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-04-10 00:00:00", 1624527666L, 'C');

              })
              .verifyComplete();
    }

    /***
     * Having max(effective_date) > max(exported_date)
     */
    @Test
    void testMaxEffDateGreaterThanMaxExportedDate() {

        // Exported date: 2021-02-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 100.00, "2021-04-12", 1614159666, 'C'));
        // Exported date: 2021-03-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.00, "2021-04-12", 1616578866, 'C'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 300.00, "2021-04-13", 1614159666, 'C'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "2021-04-02"));


        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old: 202-01-23
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "202-01-23", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");

              })
              .verifyComplete();

        // date > min exported date && < max exported date
        customerPriceRequest.setPriceRequestDate("2021-03-20");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");
              })
              .verifyComplete();

        // date > min exported date && > max exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("2021-03-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getSuccessfulItems().size());
                  assertEquals(1, result.getFailedItems().size());

                  validateFirstMinorError(result, "102020", "Price not found for given SUPC/customer combination",
                        "Price not found for SUPC: 1000001 Customer: 100001");
              })
              .verifyComplete();

        // date > min exported date && > max exported date && = min eff date
        customerPriceRequest.setPriceRequestDate("2021-04-12");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.00, "2021-04-12 00:00:00", 1616578866L, 'C');

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("2021-04-13");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 300.00, "2021-04-13 00:00:00", 1614159666L, 'C');

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("2021-04-16");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getSuccessfulItems().size());
                  assertEquals(0, result.getFailedItems().size());

                  validateFirstSuccessItem(result, "1000001", 3, 300.00, "2021-04-13 00:00:00", 1614159666L, 'C');

              })
              .verifyComplete();
    }


    /**
     * Testing invalid SUPC scenarios
     * Jira task: PRCP-2080
     */
    @Test
    void testInvalidSUPCScenarios() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "2020-02-02",
              Arrays.asList("aaa", "123", "", "~!@#$%^&*()-_+=|/.,", "-3219121",
                    "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getSuccessfulItems().size());
                  assertEquals(6, response.getFailedItems().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(0).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(1).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(2).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(3).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(4).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(5).getMessage());
              })
              .verifyComplete();


        customerPriceRequest.setProducts(Arrays.asList("aaa", "2512527"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(1, response.getSuccessfulItems().size());
                  assertEquals(1, response.getFailedItems().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedItems().get(0).getMessage());
                  validateFirstSuccessItem(response, "2512527", 1, 1.00, "2020-02-01 00:00:00", 1578960300L, 'p');
              })
              .verifyComplete();


        customerPriceRequest.setProducts(new ArrayList<>());
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getSuccessfulItems().size());
                  assertEquals(0, response.getFailedItems().size());
              })
              .verifyComplete();

        customerPriceRequest.setProducts(null);
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Codes.PRODUCTS_NOT_FOUND_IN_REQUEST, exception.getErrorCode());
                  assertEquals(Errors.Messages.MSG_PRODUCTS_NOT_FOUND_IN_REQUEST, exception.getMessage());
              });

    }

}
