package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import com.sysco.rps.service.CustomerPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 13. Jul 2020 13:01
 */
class ConversionsTest extends BaseTest {

    @Autowired
    CustomerPriceService customerPriceService;

    @Test
    void convertOpCoIdToDBField() {
        assertTrue(Conversions.convertOpCoIdToDBField(null).isEmpty());
        assertEquals(Conversions.convertOpCoIdToDBField("004"), "  004");
    }
}
