package com.sysco.rps.util;

import com.sysco.rps.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.sysco.rps.common.Constants.IS_CATCH_WEIGHT;
import static com.sysco.rps.common.Constants.PRICE_REQUEST_DATE_PATTERN;

/**
 * Various Sysco specific conversion routines.
 *
 * @author
 * @copyright (C) 2007, Sysco Corporation
 * @doc
 */
public class PricingUtils {

    private PricingUtils() {
        // default constructor
    }


    /**
     * Converts an opco id to the format required by the database.
     * SYSCO operating company numbers are numeric strings of three digits
     * padded on the left with zeroes.
     *
     * @param opCoId String
     * @return String
     */
    public static String convertOpCoIdToDBField(String opCoId) {
        return (opCoId == null) ? "" : StringUtils.leftPad(opCoId.trim(), Constants.FieldsLength.OPCO_NUMBER, "0");
    }

    public static String intToString(Integer intValue) {
        return intValue == null ? "" : Integer.toString(intValue);
    }

    public static Boolean getCatchWeightIndicator(String str) {
        return org.springframework.util.StringUtils.isEmpty(str) ? Boolean.FALSE : str.equalsIgnoreCase(IS_CATCH_WEIGHT);
    }

    public static String formatDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PRICE_REQUEST_DATE_PATTERN);
        return formatter.format(date);
    }

}
