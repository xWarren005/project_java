package com.example.s2o_mobile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.OrderItem;
import com.example.s2o_mobile.data.model.PaymentResult;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class OrderRepository {

    private final OrderApi orderApi;

    public OrderRepository() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
    }

    @Nullable
    public Call<Order> createOrder(@NonNull String tableId,
                                   @NonNull String restaurantId,
                                   @NonNull String note ) {
        String tid = tableId.trim();
        String rid = restaurantId.trim();
        String n = note == null ? "" : note.trim();

        if (tid.isEmpty()) return null;
        if (rid.isEmpty()) return null;
        return orderApi.createOrder(tid, rid, n);
    }

    @Nullable
    public Call<Order> getOrderItems(@NonNull String orderId) {
        String oid = orderId.trim();
        if (oid.isEmpty()) return null;
        return orderApi.getOrderItems(oid);
    }
    addItemToOrder(@NonNull String orderId,
                   @NonNull String foodId,
                   int quantity,
                   @Nullable String note) {
        String oid = orderId.trim();
        String fid = foodId.trim();
        String n = note == null ? "" : note.trim();

        if (oid.isEmpty()) return null;
        if (fid.isEmpty()) return null;
        if (quantity <= 0) return null;

        return orderApi.addItem(oid, fid, quantity, n);
    }

    @Nullable
    public Call<Order> updateItemQuantity(@NonNull String orderId,
                                          @NonNull String itemId,
                                          int quantity) {
        String oid = orderId.trim();
        String iid = itemId.trim();

        if (oid.isEmpty()) return null;
        if (iid.isEmpty()) return null;
        if (quantity <= 0) return null;

        return orderApi.updateItemQuantity(oid, iid, quantity);
    }

    @Nullable
    public Call<Order> removeItem(@NonNull String orderId,
                                  @NonNull String itemId) {
        String oid = orderId.trim();
        String iid = itemId.trim();

        if (oid.isEmpty()) return null;
        if (iid.isEmpty()) return null;

        return orderApi.removeItem(oid, iid);
    }

    @Nullable
    public Call<Order> updateOrderStatus(@NonNull String orderId,
                                         @NonNull String status) {
        String oid = orderId.trim();
        String st = status.trim();

        if (oid.isEmpty()) return null;
        if (st.isEmpty()) return null;

        return orderApi.updateOrderStatus(oid, st);
    }

    @Nullable
    public Call<PaymentResult> payOrder(@NonNull String orderId,
                                        @NonNull String method,
                                        @Nullable Double amount) {
        String oid = orderId.trim();
        String m = method.trim();

        if (oid.isEmpty()) return null;
        if (m.isEmpty()) return null;

        return orderApi.payOrder(oid, m, amount);
    }
}