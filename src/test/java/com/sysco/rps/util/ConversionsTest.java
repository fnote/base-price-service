package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import com.sysco.rps.service.CustomerPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        assertEquals("  004", PricingUtils.convertOpCoIdToDBField("004"));
    }

}
