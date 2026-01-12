package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.source.remote.BookingApi;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class HistoryRepository {

    private final BookingApi bookingApi;
    private final OrderApi orderApi;

    public HistoryRepository() {
        bookingApi = RetrofitClient.getInstance().create(BookingApi.class);
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
    }

    public Call<List<Booking>> getBookingHistory(String userId) {
        return bookingApi.getBookingHistory(userId);
    }

    public Call<List<Order>> getOrderHistory(String userId) {
        return orderApi.getOrderHistory(userId);
    }

    public Call<Double> getTotalSpending(String userId) {
        return orderApi.getTotalSpending(userId);
    }
}
