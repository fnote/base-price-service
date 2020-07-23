package com.sysco.rps.util;

import com.sysco.rps.common.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Various Sysco specific conversion routines.
 *
 * @author
 * @copyright (C) 2007, Sysco Corporation
 * @doc
 */
public class PricingUtils {

    private PricingUtils() {
    }

    /**
     * Right adjust a string field.
     * Creation date: (12/26/00 9:03:23 AM)
     *
     * @param inputString String
     * @param length      int
     * @return String (right adjusted input String)
     */
    private static String rightAdjustString(String inputString, int length) {
        char[] rightAdjustedValue = new char[length];
        int startMove;
        int outOffset;
        int inOffset;
        // Locate the first non-blank at the start of the input string
        for (startMove = 0; (startMove < inputString.length()) && (inputString.charAt(startMove) == ' '); startMove++) ;

        // Locate the first non-blank at the end of the input string
        for (inOffset = inputString.length() - 1; (inOffset >= 0) && (inputString.charAt(inOffset) == ' '); inOffset--)
            ;

        // Move the input string to the output area
        for (outOffset = length - 1; (outOffset >= 0) && (inOffset >= startMove); outOffset--, inOffset--)
            rightAdjustedValue[outOffset] = inputString.charAt(inOffset);

        // Fill out the front with blanks
        for (; outOffset >= 0; outOffset--)
            rightAdjustedValue[outOffset] = ' ';

        return new String(rightAdjustedValue);
    }

    /**
     * Right adjust a string field and insert a fill character for leading blanks.
     * Creation date: (12/26/00 9:03:23 AM)
     *
     * @param inputString   String
     * @param length        int
     * @param fillCharacter char
     * @return String (right adjusted input String)
     */
    private static String rightAdjustString(String inputString, int length, char fillCharacter) {
        char[] rightAdjustedValue = new char[length];
        int startMove;
        int outOffset = 0;
        int inOffset;
        // Locate the first non-blank at the start of the input string
        for (startMove = 0; (startMove < inputString.length()) && (inputString.charAt(startMove) == ' '); startMove++)

            // Locate the first non-blank at the end of the input string
            for (inOffset = inputString.length() - 1; (inOffset >= 0) && (inputString.charAt(inOffset) == ' '); inOffset--)

                // Move the input string to the output area
                for (outOffset = length - 1; (outOffset >= 0) && (inOffset >= startMove); outOffset--, inOffset--)
                    rightAdjustedValue[outOffset] = inputString.charAt(inOffset);

        // Fill out the front with blanks
        for (; outOffset >= 0; outOffset--)
            rightAdjustedValue[outOffset] = fillCharacter;

        return new String(rightAdjustedValue);
    }

    /**
     * This method safely trims a string by checking to see if it is null first.
     *
     * @param inString  String to be trimmed
     * @param nullValue String to be returned if inString is null.  If null, returns an empty string.
     * @return String trimmed or empty (never null).
     */
    public static String trimSafely(String inString, String nullValue) {
        String result;

        if (inString != null) {
            if (inString.length() > 0) {
                result = inString.trim();
            } else { // For efficiency, don't bother
                result = inString;
            }
        } else // Handle null case
        {
            result = Objects.requireNonNullElse(nullValue, "");
        }

        return result;
    }

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

    /**
     * Validates a given date String is in the format of YYYY-MM-DD and a valid date.
     *
     * @param dateStr String
     * @return boolean
     */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
