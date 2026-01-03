package com.example.s2o_mobile.utils;

import java.text.DecimalFormat;

public class PriceUtils {

    private PriceUtils() {
    }

    public static String formatVnd(double amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount) + " VND";
    }

    public static String formatVnd(long amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount) + " VND";
    }

    public static double parseNumber(String text) {
        if (text == null) return 0.0;

        String s = text.trim();
        if (s.isEmpty()) return 0.0;

        s = s.replace("VND", "")
                .replace("vnd", "")
                .replace(",", "")
                .replace(" ", "")
                .trim();

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
