package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import com.sysco.rps.service.CustomerPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    }

    @Test
    void testIsValidDate() {
        assertTrue(PricingUtils.isValidDate("2020-02-19"));
        assertTrue(PricingUtils.isValidDate("2020-02-29"));
        assertFalse(PricingUtils.isValidDate("2020-2-29"));
        assertFalse(PricingUtils.isValidDate("2020-02-1"));
        assertFalse(PricingUtils.isValidDate("2020/02/19"));
        assertFalse(PricingUtils.isValidDate("2020-13-19"));
        assertFalse(PricingUtils.isValidDate("2020-02-35"));
        assertFalse(PricingUtils.isValidDate("2019-02-29"));
        assertFalse(PricingUtils.isValidDate(""));
        assertFalse(PricingUtils.isValidDate(null));
    }
}
