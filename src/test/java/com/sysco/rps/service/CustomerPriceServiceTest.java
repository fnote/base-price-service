package com.sysco.rps.service;

import com.sysco.rps.BaseTest;
import com.sysco.rps.common.Constants;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerPriceServiceTest extends BaseTest {

    @Autowired
    CustomerPriceService customerPriceService;

    @Autowired
    TestUtilsRepository testUtilsRepository;

    private static final String ERROR_MSG_MAPPING_NOT_FOUND = "Price not found for given SUPC/customer combination. No default price found as well";

    @BeforeEach
    void setUp() {
        testUtilsRepository.truncateTables();
    }

    @AfterEach
    void tearDown() {
    }

    private void validateFirstSuccessItem(CustomerPriceResponse result, String supc, String priceZoneId, Double referencePrice,
                                          String effectiveFromDate, Long priceExportTimestamp, Boolean catchWeightIndicator) {
        Product product = new Product(supc, priceZoneId, referencePrice, effectiveFromDate, priceExportTimestamp, catchWeightIndicator);
        assertEquals(product, result.getProducts().get(0));
    }

    private void validateFirstMinorError(CustomerPriceResponse result, String supc, String errorCode, String message) {
        MinorErrorDTO error = new MinorErrorDTO(supc, errorCode, message);
        assertEquals(result.getFailedProducts().get(0), error);
    }

    @Test
    @Order(1)
    void pricesByOpCo() {
        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000002", "1000003", "1000004", "1000005"));

        testUtilsRepository.addPARecord(new PAData("1000001", 3, 100.20, "20241212", 1595229000, 'N'));

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20241212", products);

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(4, result.getFailedProducts().size());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000002", errorDTO.getSupc());

              })
              .verifyComplete();
    }

    @Test
    void testRequestingDuplicateProducts() {

        testUtilsRepository.addPARecord(new PAData("1000001", 3, 100.20, "20241212", 1595229000, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 6, "100001", "20241212"));

        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000001", "1000002", "1000002"));
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20241212", products);
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
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "20210301", 1495308212, 'N'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "20210301", 1595308212, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "20210202"));

        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20240401", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.24, "20210301", 1595308212L, Boolean.FALSE);

              })
              .verifyComplete();
    }

    /***
     * Having Multiple effective dates with a single exported date
     */
    @Test
    void testMultipleEffDatesWithSingleExportedDates() {
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "20210306", 1595308212, 'N'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "20210310", 1595308212, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "20210202"));

        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20200305", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);

              })
              .verifyComplete();

        // date = min eff Date
        customerPriceRequest.setPriceRequestDate("20210306");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.24, "20210306", 1595308212L, Boolean.FALSE);

              })
              .verifyComplete();

        // date at the middle
        customerPriceRequest.setPriceRequestDate("20210307");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.24, "20210306", 1595308212L, Boolean.FALSE);

              })
              .verifyComplete();

        // date = last eff date
        customerPriceRequest.setPriceRequestDate("20210310");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 125.24, "20210310", 1595308212L, Boolean.FALSE);

              })
              .verifyComplete();

        // date after the last eff date
        customerPriceRequest.setPriceRequestDate("20210312");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 125.24, "20210310", 1595308212L, Boolean.FALSE);

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
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 125.24, "20210410", 1616578866, 'N'));
        // Exported date: 2021-06-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.24, "20210410", 1624527666, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "20210202"));


        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20200305", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);

              })
              .verifyComplete();

        // date > min exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("20210320");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);
              })
              .verifyComplete();

        // date > min exported date && > min eff date
        customerPriceRequest.setPriceRequestDate("20210411");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.24, "20210410", 1624527666L, Boolean.FALSE);

              })
              .verifyComplete();

        // date > max exported date && > max eff date
        customerPriceRequest.setPriceRequestDate("20210625");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.24, "20210410", 1624527666L, Boolean.FALSE);

              })
              .verifyComplete();
    }

    /***
     * Having max(effective_date) > max(exported_date)
     */
    @Test
    void testMaxEffDateGreaterThanMaxExportedDate() {

        // Exported date: 2021-02-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 100.00, "20210412", 1614159666, 'N'));
        // Exported date: 2021-03-24
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 200.00, "20210412", 1616578866, 'N'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 300.00, "20210413", 1614159666, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 3, "100001", "20210402"));


        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // date too old: 2020-01-23
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20200123", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);

              })
              .verifyComplete();

        // date > min exported date && < max exported date
        customerPriceRequest.setPriceRequestDate("20210320");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);
              })
              .verifyComplete();

        // date > min exported date && > max exported date && < min eff date
        customerPriceRequest.setPriceRequestDate("20210325");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  validateFirstMinorError(result, "1000001", "102020", ERROR_MSG_MAPPING_NOT_FOUND);
              })
              .verifyComplete();

        // date > min exported date && > max exported date && = min eff date
        customerPriceRequest.setPriceRequestDate("20210412");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 200.00, "20210412", 1616578866L, Boolean.FALSE);

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("20210413");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 300.00, "20210413", 1614159666L, Boolean.FALSE);

              })
              .verifyComplete();

        // date > min exported date && > max exported date && > min eff date && < max eff date
        customerPriceRequest.setPriceRequestDate("20210416");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(0, result.getFailedProducts().size());

                  validateFirstSuccessItem(result, "1000001", "3", 300.00, "20210413", 1614159666L, Boolean.FALSE);

              })
              .verifyComplete();
    }


    /**
     * Testing invalid SUPC scenarios
     * Jira task: PRCP-2080
     */
    @Test
    @Tag("desc:PRCP-2080")
    void testInvalidSUPCScenarios() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "20200202",
              Arrays.asList("aaa", "123", "", "~!@#$%^&*()-_+=|/.,", "-3219121", "20001.47", "0", "null",
                    "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(9, response.getFailedProducts().size());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(1).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(2).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(3).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(4).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(5).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(6).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(7).getMessage());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(8).getMessage());
              })
              .verifyComplete();


        customerPriceRequest.setProducts(Arrays.asList("aaa", "2512527"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(1, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
                  validateFirstSuccessItem(response, "2512527", "1", 1.00, "20200201", 1578960300L, Boolean.TRUE);
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
    @Tag("desc:PRCP-2085")
    void testInvalidSUPCWithValidSUPCList() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        // SUPC -> 7565088 is not in the database
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "20200202",
              Arrays.asList("2512527", "7565045", "2000000", "3982204")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());

                  assertEquals(new MinorErrorDTO("2000000", "102020", ERROR_MSG_MAPPING_NOT_FOUND), response.getFailedProducts().get(0));
              })
              .verifyComplete();

        customerPriceRequest.setProducts(Arrays.asList("2512527", "7565045", "2000002", "3982204"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());

                  assertEquals(new MinorErrorDTO("2000002", "102020", ERROR_MSG_MAPPING_NOT_FOUND),
                        response.getFailedProducts().get(0));

              })
              .verifyComplete();

        customerPriceRequest.setProducts(Arrays.asList("2512527", "7565045", "$20001.47", "3982204"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getProducts().size());
                  assertEquals(1, response.getFailedProducts().size());

                  assertEquals(new MinorErrorDTO("$20001.47", "102020", ERROR_MSG_MAPPING_NOT_FOUND),
                        response.getFailedProducts().get(0));
              })
              .verifyComplete();
    }

    /**
     * Testing invalid SUPC scenarios
     * Jira task: PRCP-2086
     */
    @Test
    @Tag("desc:PRCP-2086")
    void testAllSUPCsInvalid() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        // SUPC -> 7565088, 3982205 are not in the database
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "20200202",
              Arrays.asList("7565088", "3982205")
        );
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(2, response.getFailedProducts().size());

                  assertEquals(new MinorErrorDTO("7565088", "102020", ERROR_MSG_MAPPING_NOT_FOUND), response.getFailedProducts().get(0));
                  assertEquals(new MinorErrorDTO("3982205", "102020", ERROR_MSG_MAPPING_NOT_FOUND), response.getFailedProducts().get(1));
              })
              .verifyComplete();

        customerPriceRequest.setProducts(Arrays.asList("test", "$20001.47", "A2000002"));
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(0, response.getProducts().size());
                  assertEquals(3, response.getFailedProducts().size());

                  assertEquals(new MinorErrorDTO("test", "102020", ERROR_MSG_MAPPING_NOT_FOUND),
                        response.getFailedProducts().get(0));
                  assertEquals(new MinorErrorDTO("$20001.47", "102020", ERROR_MSG_MAPPING_NOT_FOUND),
                        response.getFailedProducts().get(1));
                  assertEquals(new MinorErrorDTO("A2000002", "102020", ERROR_MSG_MAPPING_NOT_FOUND),
                        response.getFailedProducts().get(2));

              })
              .verifyComplete();
    }

    /**
     * Testing invalid Date scenarios
     * Jira task: PRCP-2083
     */
    @Test
    @Tag("desc:PRCP-2083")
    void testInvalidDateScenarios() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "00200202",
              Arrays.asList("2512527", "7565045", "3982204")
        );

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 1);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("20090225");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getFailedProducts().size());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, response.getFailedProducts().get(0).getMessage());
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("50200515");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(3, response.getProducts().size());
              })
              .verifyComplete();

        List<String> invalidDates = Arrays.asList(
              "2019329",
              "2019029",
              "0090225",
              "090225",
              "90225",
              "20190229",
              "20191329",
              "20191035",
              "2019/10/10",
              "10/13/2019",
              "",
              null,
              "null",
              "2^201&1*",
              "1542020",
              "15202015",
              "4152020",
              "2020May15",
              "2020 10 15",
              "2020.10.15",
              "2020:10:15"
        );

        for (String invalidDate : invalidDates) {
            customerPriceRequest.setPriceRequestDate(invalidDate);
            verifyInvalidDateException(customerPriceRequest);
        }

    }

    private void verifyInvalidDateException(CustomerPriceRequest customerPriceRequest) {
        Mono<CustomerPriceResponse> customerPriceResponseMono;
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
    @Tag("desc:PRCP-2093")
    void testMultipleEffectiveDates() {
        testUtilsRepository.addPARecordsFromCsv("PA_BulkData.csv", true);
        testUtilsRepository.addPriceZoneRecordsFromCsv("EATS_BulkData.csv");

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020",
              "68579367", "20200210",
              Collections.singletonList("3982206")
        );

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", "5", 50.00,
                              "20200201", 1580947560L, Boolean.TRUE),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();


        customerPriceRequest.setPriceRequestDate("20200211");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", "5", 50.00,
                              "20200201", 1580947560L, Boolean.TRUE),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("20200220");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", "4", 30.00,
                              "20200205", 1580947560L, Boolean.FALSE),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("20200221");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(response -> {
                  assertEquals(
                        new Product("3982206", "4", 30.00,
                              "20200205", 1580947560L, Boolean.FALSE),
                        response.getProducts().get(0)
                  );
              })
              .verifyComplete();

        customerPriceRequest.setPriceRequestDate("20200209");
        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, null);
        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(response -> {
                    assertEquals(
                            new Product("3982206", "3", 23.00,
                                    "20200201", 1580947560L, Boolean.TRUE),
                            response.getProducts().get(0)
                    );
                })
                .verifyComplete();

    }

    /**
     * Testing out the default prices
     */
    @Test
    void testDefaultPrices() {

        List<String> products = new ArrayList<>(Arrays.asList("1000001", "1000002"));
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20241212", products);
        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        // with no data, should receive minor errors for both SUPCs
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(2, result.getFailedProducts().size());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // Adding a PA record only with a price zone other than 3 should return minor errors for both SUPCs
        testUtilsRepository.addPARecord(new PAData("1000001", 2, 200.20, "20241212", 1595229000, 'N'));

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(0, result.getProducts().size());
                  assertEquals(2, result.getFailedProducts().size());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // Adding a PA record only with a price for price zone 3 should return the default price
        testUtilsRepository.addPARecord(new PAData("1000002", 3, 100.20, "20201212", 1595229000, 'N'));

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  Product product = result.getProducts().get(0);
                  assertEquals("1000002", product.getSupc());
                  assertEquals("3", product.getPriceZoneId());
                  assertEquals(100.20, product.getReferencePrice());
                  assertEquals("20201212", product.getEffectiveFromDate());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // Adding another PA record with a price for price zone 3 should return the default price of the largest effective date
        testUtilsRepository.addPARecord(new PAData("1000002", 3, 200.20, "20221212", 1595229000, 'N'));

        // with no data, should receive minor errors for both SUPCs
        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  Product product = result.getProducts().get(0);
                  assertEquals("1000002", product.getSupc());
                  assertEquals("3", product.getPriceZoneId());
                  assertEquals(200.20, product.getReferencePrice());
                  assertEquals("20221212", product.getEffectiveFromDate());
                  assertEquals(1595229000L, product.getPriceExportTimestamp());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // Adding another PA record with a price for price zone 3 should return the default price of the largest effective date AND largest
        // exported date
        testUtilsRepository.addPARecord(new PAData("1000002", 3, 300.20, "20221212", 1595249000, 'N'));

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  Product product = result.getProducts().get(0);
                  assertEquals("1000002", product.getSupc());
                  assertEquals("3", product.getPriceZoneId());
                  assertEquals(300.20, product.getReferencePrice());
                  assertEquals("20221212", product.getEffectiveFromDate());
                  assertEquals(1595249000L, product.getPriceExportTimestamp());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // Adding a PRICE_ZONE record with a price zone that is non existing, should return the default price (price of price zone 3)
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000002", 1, "100001", "20201212"));

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  Product product = result.getProducts().get(0);
                  assertEquals("1000002", product.getSupc());
                  assertEquals("3", product.getPriceZoneId());
                  assertEquals(300.20, product.getReferencePrice());
                  assertEquals("20221212", product.getEffectiveFromDate());
                  assertEquals(1595249000L, product.getPriceExportTimestamp());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();

        // having both PRICE and PRICE_ZONE data should return correct results
        testUtilsRepository.addPARecord(new PAData("1000002", 2, 400.20, "20221212", 1595249000, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000002", 2, "100001", "20221212"));

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  assertNotNull(result);
                  assertEquals(1, result.getProducts().size());
                  assertEquals(1, result.getFailedProducts().size());

                  Product product = result.getProducts().get(0);
                  assertEquals("1000002", product.getSupc());
                  assertEquals("2", product.getPriceZoneId());
                  assertEquals(400.20, product.getReferencePrice());
                  assertEquals("20221212", product.getEffectiveFromDate());
                  assertEquals(1595249000L, product.getPriceExportTimestamp());

                  MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                  assertEquals("102020", errorDTO.getErrorCode());
                  assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                  assertEquals("1000001", errorDTO.getSupc());
              })
              .verifyComplete();
    }

    /**
     * Testing correct price zone table selecting logic
     * PRICE_ZONE_MASTER_DATE table contains these table activation details
     *          TABLE_TYPE, TABLE_NAME, EFFECTIVE_DATE
     *         ('ACTIVE', 'PRICE_ZONE_01', '2020-01-01')
     *         ('HISTORY', 'PRICE_ZONE_02', '2019-01-01')
     */
    @Test
    void testMultiplePriceZoneTables() {
        testUtilsRepository.addPARecord(new PAData("1000001", 1, 100.00, "20180101", 1514764800, 'N'));
        testUtilsRepository.addPARecord(new PAData("1000001", 2, 200.00, "20180101", 1514764800, 'N'));
        testUtilsRepository.addPARecord(new PAData("1000001", 3, 300.00, "20180101", 1514764800, 'N'));
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 2, "100001", "20190101"), Constants.DBNames.PRICE_ZONE_02);
        testUtilsRepository.addPriceZoneRecord(new PriceZoneData("1000001", 1, "100001", "20200101"));

        List<String> products = new ArrayList<>(Collections.singletonList("1000001"));

        // price request date is after the active table's effective date (should query the ACTIVE table)
        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "20200323", products);

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(100.00, result.getProducts().get(0).getReferencePrice());
                })
                .verifyComplete();

        // price request date is on the active table's effective date (should query the ACTIVE table)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20200101", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(100.00, result.getProducts().get(0).getReferencePrice());
                })
                .verifyComplete();

        // price request date is before the active table's effective date (should query the HISTORY table)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20191231", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(200.00, result.getProducts().get(0).getReferencePrice());

                })
                .verifyComplete();

        // price request date is on the history table's effective date (should query the HISTORY table)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20190101", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(200.00, result.getProducts().get(0).getReferencePrice());
                })
                .verifyComplete();

        // price request date is before the history table's effective date (should use the default price zone which is 3)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20181231", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(300.00, result.getProducts().get(0).getReferencePrice());
                })
                .verifyComplete();

        // price request date is on default price record's effective date (should use the default price zone which is 3)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20180101", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getProducts().size());
                    assertEquals(0, result.getFailedProducts().size());
                    assertEquals(300.00, result.getProducts().get(0).getReferencePrice());
                })
                .verifyComplete();

        // price request date is before default price record's effective date (a price should not be returned since there isn't valid price record for default price zone)
        customerPriceRequest = new CustomerPriceRequest("020", "100001", "20171231", products);

        customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(0, result.getProducts().size());
                    assertEquals(1, result.getFailedProducts().size());

                    MinorErrorDTO errorDTO = result.getFailedProducts().get(0);
                    assertEquals("102020", errorDTO.getErrorCode());
                    assertEquals(ERROR_MSG_MAPPING_NOT_FOUND, errorDTO.getMessage());
                    assertEquals("1000001", errorDTO.getSupc());
                })
                .verifyComplete();

    }

}
