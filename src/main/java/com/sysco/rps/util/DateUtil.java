package com.sysco.rps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/3/20 Time: 12:54 PM
 */

public class DateUtil {

    private DateUtil() {
    }

    public static Date toDate(String date, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(date);

    }

    public static String toDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.format(date);
    }
}
