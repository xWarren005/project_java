package com.example.s2o_mobile.utils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_DATE_TIME_FULL = "dd/MM/yyyy HH:mm:ss";

    private DateUtils() {
    }

    public static String format(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
                .format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault())
                .format(date);
    }

    public static String formatFull(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DATE_TIME_FULL, Locale.getDefault())
                .format(date);
    }
}
public static Date parse(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date parseDateTime(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date parseFull(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE_TIME_FULL, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date now() {
    return new Date();
}


public static Date parse(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date parseDateTime(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date parseFull(String value) {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        return new SimpleDateFormat(FORMAT_DATE_TIME_FULL, Locale.getDefault())
                .parse(value);
    } catch (ParseException e) {
        return null;
    }
}

public static Date now() {
    return new Date();
}

