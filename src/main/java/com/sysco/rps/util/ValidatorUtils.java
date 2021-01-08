package com.sysco.rps.util;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class to hold util methods for data validation
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2021, Sysco Corporation
 * @doc
 * @end Created : 07. Jan 2021 5:08 PM
 */
public class ValidatorUtils {

    private ValidatorUtils() {
        //default constructor
    }

    /**
     * Strictly checking the given date string against the date format
     */
    public static boolean isValidDate(String dateStr, String datePattern) {

        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(datePattern)) {
            return false;
        }

        DateFormat sdf = new SimpleDateFormat(datePattern);
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }

        // we are strictly checking the dateString with the date pattern. If the legnth is not checked values like 2019039 (2019 03 9) will pass
        return (datePattern.length() == dateStr.length());
    }

}
