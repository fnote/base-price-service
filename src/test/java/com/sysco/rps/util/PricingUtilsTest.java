package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 13. Jul 2020 13:01
 */
@SpringBootTest
class PricingUtilsTest extends BaseTest {

    @Test
    void convertOpCoIdToDBField() {
        assertTrue(PricingUtils.convertOpCoIdToDBField(null).isEmpty());
        assertEquals("001", PricingUtils.convertOpCoIdToDBField("1"));
        assertEquals("002", PricingUtils.convertOpCoIdToDBField("02"));
        assertEquals("030", PricingUtils.convertOpCoIdToDBField("030"));
        assertEquals("400", PricingUtils.convertOpCoIdToDBField("400"));
    }

    @Test
    void intToString() {
        assertTrue(PricingUtils.intToString(null).isEmpty());
        assertEquals("1", PricingUtils.intToString(1));
        assertEquals("1000", PricingUtils.intToString(1000));
    }

    @Test
    void getCatchWeightIndicator() {
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator(null));
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator("C"));
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator("P"));
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator("X"));
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator("N"));
        assertEquals(Boolean.FALSE, PricingUtils.getCatchWeightIndicator("n"));
        assertEquals(Boolean.TRUE, PricingUtils.getCatchWeightIndicator("Y"));
        assertEquals(Boolean.TRUE, PricingUtils.getCatchWeightIndicator("y"));
    }

    @Test
    void formatDate() {
        assertTrue(PricingUtils.formatDate(null).isEmpty());
        assertEquals("20200827", PricingUtils.formatDate(LocalDateTime.of(2020, 8, 27, 15, 56)));
    }

}
