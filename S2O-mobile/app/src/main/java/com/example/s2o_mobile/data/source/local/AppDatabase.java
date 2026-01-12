package com.example.s2o_mobile.data.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.model.User;

@Database(
        entities = {
                User.class,
                Restaurant.class,
                Booking.class,
                Order.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "s2o_local.db";
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract BookingDao bookingDao();

    public abstract OrderDao orderDao();

    public abstract FavoriteDao favoriteDao();
}
