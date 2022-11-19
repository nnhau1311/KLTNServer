package com.example.childrenhabitsserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateTimeUtils {
    public static final String DATE_FORMAT_DDMMYYYY = "dd-MM-yyyy";
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public static Date addDate(Date date, int amount) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, amount);
        return c.getTime();
    }

    public static String convertDateToString(Date date, String pattern) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return new SimpleDateFormat(pattern).format(date);
    }
}
