package com.example.s2o_mobile.ui.order.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.repository.OrderRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailViewModel extends ViewModel {

    public static class OrderHeader {
        public String orderCode;
        public String status;
        public String timeText;
        public double totalAmount;
    }

    private final OrderRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<OrderHeader> orderHeader = new MutableLiveData<>(null);
    private final MutableLiveData<List<Order.OrderItem>> orderItems =
            new MutableLiveData<>(Collections.emptyList());

    public OrderDetailViewModel(@NonNull OrderRepository repository) {
        this.repository = repository;
    }

    public OrderDetailViewModel() {
        this.repository = new OrderRepository();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<OrderHeader> getOrderHeader() {
        return orderHeader;
    }

    public LiveData<List<Order.OrderItem>> getOrderItems() {
        return orderItems;
    }

    public void loadOrderDetail(int orderId) {
        if (orderId <= 0) {
            error.setValue("Invalid order id");
            return;
        }

        Call<Order> call = repository.getOrderItems(String.valueOf(orderId));
        if (call == null) {
            error.setValue("Cannot create request");
            return;
        }

        loading.setValue(true);
        error.setValue(null);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                loading.setValue(false);
                if (!response.isSuccessful() || response.body() == null) {
                    error.setValue("Load order failed");
                    return;
                }

                Order order = response.body();
                orderItems.setValue(order.getItems());

                OrderHeader header = new OrderHeader();
                header.orderCode = String.valueOf(order.getId());
                header.status = safe(order.getStatus());
                header.timeText = safe(order.getCreatedAt());
                header.totalAmount = order.getTotalAmount();
                if (header.totalAmount <= 0) {
                    header.totalAmount = order.calcTotalAmount();
                }
                orderHeader.setValue(header);
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage() == null ? "Network error" : t.getMessage());
            }
        });
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
