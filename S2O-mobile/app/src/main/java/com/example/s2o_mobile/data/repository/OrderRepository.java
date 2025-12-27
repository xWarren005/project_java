package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.OrderItem;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class OrderRepository {

    private final OrderApi orderApi;

    public OrderRepository() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
    }

    public Call<Order> createOrder(String tableId, String restaurantId) {
        return orderApi.createOrder(tableId, restaurantId);
    }

    public Call<List<OrderItem>> getOrderItems(String orderId) {
        return orderApi.getOrderItems(orderId);
    }

    public Call<Order> addItem(String orderId, String foodId, int quantity) {
        return orderApi.addItem(orderId, foodId, quantity);
    }

    public Call<Order> updateItemQuantity(String orderId, String itemId, int quantity) {
        if (orderId == null || itemId == null) return null;
        return orderApi.updateItemQuantity(orderId, itemId, quantity);
    }

    public Call<Order> removeItemFromOrder(String orderId, String itemId) {
        if (orderId == null || itemId == null) return null;
        return orderApi.removeItem(orderId, itemId);
    }

    public Call<Order> updateOrderStatus(String orderId, String status) {
        if (orderId == null || status == null) return null;
        return orderApi.updateOrderStatus(orderId, status);
    }

    public Call<PaymentResult> payOrder(String orderId, String method) {
        if (orderId == null) return null;
        return orderApi.payOrder(orderId, method, null);
    }

}
