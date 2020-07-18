package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.service.CustomerPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 13. Jul 2020 13:01
 */
@SpringBootTest
class ConversionsTest extends BaseTest {

    @Autowired
    CustomerPriceService customerPriceService;

    @Test
    void convertOpCoIdToDBField() {
        assertTrue(PricingUtils.convertOpCoIdToDBField(null).isEmpty());
        assertEquals(PricingUtils.convertOpCoIdToDBField("004"), "  004");

        List<String> products = new ArrayList(Arrays.asList("1000001", "1000002", "1000003", "1000004", "1000005"));

        CustomerPriceRequest customerPriceRequest = new CustomerPriceRequest("020", "100001", "2024-12-12", products);

        Mono<CustomerPriceResponse> customerPriceResponseMono = customerPriceService.pricesByOpCo(customerPriceRequest, 10);

        StepVerifier.create(customerPriceResponseMono)
              .consumeNextWith(result -> {
                  System.out.println(result);
              })
              .verifyComplete();
    }
}
