package com.example.s2o_mobile.utils;

import android.util.Patterns;

public class Validator {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.matches("^0[0-9]{9}$");
    }

    public static boolean isValidName(String name) {
        if (isEmpty(name)) return false;
        return name.length() >= 2;
    }
}
