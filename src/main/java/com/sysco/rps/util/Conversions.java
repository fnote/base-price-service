package com.sysco.rps.util;

import com.sysco.rps.common.Constants;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 21. Jun 2020 09:17
 */
public class Conversions {

    /**
     * Converts an opco id to the format required by the database.
     * SYSCO operating company numbers are numeric strings of three digits
     * padded on the left with zeroes and with two leading spaces
     * for a total length of five characters such as "  001".
     *
     * @param opcoId String
     * @return String
     */
    public static String convertOpCoIdToDBField(String opcoId) {
        if (opcoId == null) {
            return "";
        } else {
            String opcoNumber = opcoId.trim();

            if (opcoNumber.length() < Constants.FieldsLength.OPCO_NUMBER) {
                opcoNumber = PricingUtils.rightAdjustString(opcoNumber, Constants.FieldsLength.OPCO_NUMBER, '0');
            }

            return PricingUtils.rightAdjustString(opcoNumber, Constants.FieldsLength.OPCO_ID_OVERALL);
        }
    }
}
