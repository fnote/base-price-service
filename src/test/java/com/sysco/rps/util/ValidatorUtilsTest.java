package com.sysco.rps.util;

import com.sysco.rps.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.sysco.rps.common.Constants.PRICE_REQUEST_DATE_PATTERN;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2021,
 * @doc
 * @end Created : 08. Jan 2021 11:24 AM
 */
class ValidatorUtilsTest extends BaseTest {

    @Test
    void isValidDate() {

        List<String> valid = Arrays.asList(
              "20210108",
              "20201231",
              "19960229"
        );

        for (String validDate : valid) {
            assertTrue(ValidatorUtils.isValidDate(validDate, PRICE_REQUEST_DATE_PATTERN), "Should be valid: " + validDate);
        }


        List<String> invalidDates = Arrays.asList(
              "20201131",
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
            assertFalse(ValidatorUtils.isValidDate(invalidDate, PRICE_REQUEST_DATE_PATTERN), "Should be invalid: " + invalidDate);
        }

    }
}
