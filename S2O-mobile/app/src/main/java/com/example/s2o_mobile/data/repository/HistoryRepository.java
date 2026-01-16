package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.Restaurant;

import java.util.Collections;
import java.util.List;

public class HistoryRepository {

    public void getBookingHistory(RepositoryCallback<List<Booking>> callback) {
        if (callback != null) {
            callback.onSuccess(Collections.emptyList());
        }
    }

    public void getOrderHistory(RepositoryCallback<List<Order>> callback) {
        if (callback != null) {
            callback.onSuccess(Collections.emptyList());
        }
    }

    public void getRestaurantHistory(RepositoryCallback<List<Restaurant>> callback) {
        if (callback != null) {
            callback.onSuccess(Collections.emptyList());
        }
    }
}