package com.sysco.rps.service;

import com.sysco.rps.BaseTest;
import com.sysco.rps.common.Errors;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.MinorErrorDTO;
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
        assertEquals(product, result.getProducts().get(0));
    }

    private void validateFirstMinorError(CustomerPriceResponse result, String supc, String errorCode, String message) {
        MinorErrorDTO error = new MinorErrorDTO(supc, errorCode, message);
        assertEquals(result.getFailedProducts().get(0), error);
    }


    @Test
    void pricesByOpCo() {
        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000002", "1000003", "1000004", "1000005"));

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2024-12-12", products);

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(5, result.getFailedProducts().size());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals("Price not found for given SUPC/customer combination", errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());

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
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

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
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-01", 1595308212L, 'C');

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
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");

              })
              .verifyComplete();

        // date = min eff Date
        customerPriceRequest.setPriceRequestDate("2021-03-06");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-06", 1595308212L, 'C');

              })
              .verifyComplete();

        // date at the middle
        customerPriceRequest.setPriceRequestDate("2021-03-07");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-03-06", 1595308212L, 'C');

              })
              .verifyComplete();

        // date = last eff date
        customerPriceRequest.setPriceRequestDate("2021-03-10");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 125.24, "2021-03-10", 1595308212L, 'C');

              })
              .verifyComplete();

        // date after the last eff date
        customerPriceRequest.setPriceRequestDate("2021-03-12");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 125.24, "2021-03-10", 1595308212L, 'C');

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
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");

              })
              .verifyComplete();

        // date > min exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("2021-03-20");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");
              })
              .verifyComplete();

        // date > min exported date && > min eff date
        customerPriceRequest.setPriceRequestDate("2021-04-11");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-04-10", 1624527666L, 'C');

              })
              .verifyComplete();

        // date > max exported date && > max eff date
        customerPriceRequest.setPriceRequestDate("2021-06-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.24, "2021-04-10", 1624527666L, 'C');

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
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");

              })
              .verifyComplete();

        // date > min exported date && < max exported date
        customerPriceRequest.setPriceRequestDate("2021-03-20");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");
              })
              .verifyComplete();

        // date > min exported date && > max exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("2021-03-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", "Price not found for given SUPC/customer combination");
              })
              .verifyComplete();

        // date > min exported date && > max exported date && = min eff date
        customerPriceRequest.setPriceRequestDate("2021-04-12");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 200.00, "2021-04-12", 1616578866L, 'C');

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("2021-04-13");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 300.00, "2021-04-13", 1614159666L, 'C');

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("2021-04-16");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", 3, 300.00, "2021-04-13", 1614159666L, 'C');

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
              Arrays.asList("aaa", "123", "", "~!@#$%^&*()-_+=|/.,", "-3219121", "20001.47", "0", "null",
                    "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(9, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(1).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(2).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(3).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(4).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(5).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(6).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(7).getMessage());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(8).getMessage());
              })
              .verifyComplete();


        customerPriceRequest.setProducts(Arrays.asList("aaa", "2512527"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(1, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
                  validateFirstSuccessItem(response, "2512527", 1, 1.00, "2020-02-01", 1578960300L, 'p');
              })
              .verifyComplete();


        customerPriceRequest.setProducts(new ArrayList<>());
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(0, response.getFailedProducts().size());
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

    /**
     * Testing invalid Date scenarios
     * Jira task: PRCP-2085
     */
    @Test
    void testInvalidSUPCWithValidSUPCList() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        // SUPC -> 7565088 is not in the database
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "2020-02-02",
              Arrays.asList("2512527", "7565045", "7565088", "3982204")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());

                  String errorMsg = "Price not found for given SUPC/customer combination";
                  assertEquals(new MinorErrorDTO("7565088", "102020", errorMsg), response.getFailedProducts().get(0));

              })
              .verifyComplete();
    }


    /**
     * Testing invalid SUPC scenarios
     * Jira task: PRCP-2086
     */
    @Test
    void testAllSUPCsInvalid() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        // SUPC -> 7565088, 3982205 are not in the database
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "2020-02-02",
              Arrays.asList("7565088", "3982205")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(2, response.getFailedProducts().size());

                  String errorData = "Price not found for SUPC: %s Customer: %s";
                  String errorMsg = "Price not found for given SUPC/customer combination";
                  assertEquals(new MinorErrorDTO("7565088", "102020", errorMsg), response.getFailedProducts().get(0));
                  assertEquals(new MinorErrorDTO("3982205", "102020", errorMsg), response.getFailedProducts().get(1));
              })
              .verifyComplete();

    }

    /**
     * Testing invalid Date scenarios
     * Jira task: PRCP-2083
     */

    @Test
    void testInvalidDateScenarios() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "0020-02-02",
              Arrays.asList("2512527", "7565045", "3982204")
        );

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2019-3-29");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2019-02-9");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2009-02-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("009-02-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("09-02-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("9-02-25");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(Errors.Messages.MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2019-02-29");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2019-13-29");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2019-10-35");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2019/10/10");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("10/13/2019");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate(null);
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("null");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });


        customerPriceRequest.setPriceRequestDate("2^20-1&-1*");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("15-4-2020");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("15-2020-15");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("4-15-2020");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2020-May-15");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2020 10 15");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2020.10.15");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });

        customerPriceRequest.setPriceRequestDate("2020:10:15");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .verifyErrorSatisfies(error -> {
                  RefPriceAPIException exception = (RefPriceAPIException) error;
                  assertEquals(Errors.Messages.MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getMessage());
                  assertEquals(Errors.Codes.INVALID_PRICE_REQUEST_DATE_IN_REQUEST, exception.getErrorCode());
              });


    }

    /**
     * Testing invalid Date scenarios
     * Jira task: PRCP-2093
     */
    @Test
    void testMultipleEffectiveDates() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "2020-02-01",
              Collections.singletonList("3982206")
        );

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", 4, 28.00,
                              "2020-02-01", 1580947560L, 'p'),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();


        customerPriceRequest.setPriceRequestDate("2020-02-02");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", 4, 28.00,
                              "2020-02-01", 1580947560L, 'p'),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2020-02-05");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", 4, 30.00,
                              "2020-02-05", 1580947560L, 'c'),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("2020-02-06");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", 4, 30.00,
                              "2020-02-05", 1580947560L, 'c'),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

    }

}
