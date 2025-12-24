package com.example.s2o_mobile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.source.remote.BookingApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import retrofit2.Call;

public class BookingRepository {

    private final BookingApi bookingApi;

    public BookingRepository() {
        bookingApi = RetrofitClient.getInstance().create(BookingApi.class);
    }

    @Nullable
    public Call<Booking> createBooking(@NonNull String restaurantId,
                                       @NonNull String bookingTime,
                                       int peopleCount,
                                       @Nullable String note) {

        String rid = restaurantId.trim();
        String time = bookingTime.trim();
        String n = note == null ? "" : note.trim();

        if (rid.isEmpty()) return null;
        if (time.isEmpty()) return null;
        if (peopleCount <= 0) return null;

        return bookingApi.createBooking(rid, time, peopleCount, n);
    }

    @Nullable
    public Call<Booking> getBookingStatus(@NonNull String bookingId) {
        String bid = bookingId.trim();
        if (bid.isEmpty()) return null;
        return bookingApi.getBookingStatus(bid);
    }
}
