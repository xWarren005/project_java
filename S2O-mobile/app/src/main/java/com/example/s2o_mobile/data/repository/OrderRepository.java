package com.example.s2o_mobile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.model.PaymentResult;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;
import com.example.s2o_mobile.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.lifecycle.MutableLiveData;

public class OrderRepository {

    private final OrderApi orderApi;
    private final SessionManager sessionManager;

    public OrderRepository() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        sessionManager = null;
    }

    private OrderRepository(SessionManager sessionManager) {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        this.sessionManager = sessionManager;
    }

    public static OrderRepository getInstance(SessionManager sessionManager) {
        return new OrderRepository(sessionManager);
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
    public Call<List<MenuItem>> getMenu(@NonNull String restaurantId) {
        String rid = restaurantId.trim();
        if (rid.isEmpty()) return null;
        try {
            int id = Integer.parseInt(rid);
            return orderApi.getMenu(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public Call<Order> getOrderItems(@NonNull String orderId) {
        String oid = orderId.trim();
        if (oid.isEmpty()) return null;
        return orderApi.getOrderItems(oid);
    }

    @Nullable
    public Call<Order> addItemToOrder(@NonNull String orderId,
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

    public void getMyOrders(MutableLiveData<List<Order>> orders,
                            MutableLiveData<String> errorMessage,
                            MutableLiveData<Boolean> loading) {
        if (loading != null) loading.setValue(true);
        if (errorMessage != null) errorMessage.setValue(null);

        String token = sessionManager == null ? "" : sessionManager.getAuthToken();
        String bearer = token == null || token.trim().isEmpty() ? "" : "Bearer " + token.trim();

        Call<List<Order>> call = orderApi.getMyOrders(bearer);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (loading != null) loading.setValue(false);
                if (!response.isSuccessful()) {
                    if (errorMessage != null) {
                        errorMessage.setValue("Tải đơn hàng thất bại (" + response.code() + ").");
                    }
                    return;
                }
                if (orders != null) {
                    List<Order> body = response.body();
                    orders.setValue(body == null ? java.util.Collections.emptyList() : body);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                if (loading != null) loading.setValue(false);
                if (errorMessage != null) {
                    String msg = t.getMessage();
                    errorMessage.setValue(msg == null ? "Lỗi kết nối. Vui lòng thử lại." : msg);
                }
            }
        });
    }
}
