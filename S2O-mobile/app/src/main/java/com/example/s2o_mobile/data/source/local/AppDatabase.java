package com.example.s2o_mobile.data.source.local;

import android.content.Context;

public class AppDatabase {

    private static AppDatabase instance;

    private AppDatabase() {
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase();
        }
        return instance;
    }

    public UserDao userDao() {
        return null;
    }

    public BookingDao bookingDao() {
        return null;
    }

    public OrderDao orderDao() {
        return null;
    }

    public FavoriteDao favoriteDao() {
        return null;
    }
}