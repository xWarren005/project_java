package com.example.s2o_mobile.ui.order.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.repository.OrderRepository;

import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private Call<Order> runningCall;
    private final OrderRepository orderRepository;

    public CartViewModel() {
        this.orderRepository = new OrderRepository();
    }

    public CartViewModel(@NonNull OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    private final MutableLiveData<List<CartItem>> cartItems =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Integer> totalQuantity = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Integer> getTotalQuantity() {
        return totalQuantity;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addItem(@NonNull String foodId,
                        @NonNull String name,
                        double unitPrice,
                        int quantity) {
        if (foodId.trim().isEmpty()) return;
        if (quantity <= 0) return;

        List<CartItem> current = snapshot();
        int idx = indexOf(current, foodId);

        if (idx >= 0) {
            CartItem old = current.get(idx);
            current.set(idx, old.withQuantity(old.quantity + quantity));
        } else {
            current.add(new CartItem(foodId.trim(), name, unitPrice, quantity, ""));
        }

        updateCart(current);
    }

    public void updateQuantity(@NonNull String foodId, int quantity) {
        if (foodId.trim().isEmpty()) return;

        List<CartItem> current = snapshot();
        int idx = indexOf(current, foodId);

        if (idx < 0) return;

        if (quantity <= 0) {
            current.remove(idx);
        } else {
            CartItem old = current.get(idx);
            current.set(idx, old.withQuantity(quantity));
        }

        updateCart(current);
    }

    public void updateNote(@NonNull String foodId, @Nullable String note) {
        if (foodId.trim().isEmpty()) return;

        List<CartItem> current = snapshot();
        int idx = indexOf(current, foodId);

        if (idx < 0) return;

        CartItem old = current.get(idx);
        current.set(idx, old.withNote(note == null ? "" : note.trim()));

        updateCart(current);
    }

    public void removeItem(@NonNull String foodId) {
        if (foodId.trim().isEmpty()) return;

        List<CartItem> current = snapshot();
        int idx = indexOf(current, foodId);

        if (idx >= 0) {
            current.remove(idx);
            updateCart(current);
        }
    }

    public void clearCart() {
        updateCart(new ArrayList<>());
    }

    public boolean isEmpty() {
        List<CartItem> list = cartItems.getValue();
        return list == null || list.isEmpty();
    }

    private List<CartItem> snapshot() {
        List<CartItem> list = cartItems.getValue();
        if (list == null) return new ArrayList<>();
        return new ArrayList<>(list);
    }

    private int indexOf(List<CartItem> list, String foodId) {
        for (int i = 0; i < list.size(); i++) {
            CartItem it = list.get(i);
            if (it != null && foodId.equals(it.foodId)) return i;
        }
        return -1;
    }

    private void updateCart(List<CartItem> newList) {
        cartItems.setValue(newList == null ? Collections.emptyList() : newList);
        recalcTotals(newList);
    }

    private void recalcTotals(List<CartItem> list) {
        int qty = 0;
        double sum = 0.0;

        if (list != null) {
            for (CartItem it : list) {
                if (it == null) continue;
                qty += Math.max(it.quantity, 0);
                sum += (it.unitPrice * Math.max(it.quantity, 0));
            }
        }

        totalQuantity.setValue(qty);
        totalPrice.setValue(sum);
    }
    public void placeOrder(@NonNull String tableId,
                           @NonNull String restaurantId,
                           @Nullable String note) {

        List<CartItem> items = snapshot();
        if (items.isEmpty()) {
            errorMessage.setValue("Giỏ hàng đang trống.");
            return;
        }
        if (tableId.trim().isEmpty() || restaurantId.trim().isEmpty()) {
            errorMessage.setValue("Thiếu thông tin bàn/nhà hàng.");
            return;
        }

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Order> call = orderRepository.createOrder(
                tableId.trim(),
                restaurantId.trim(),
                note
        );

        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tạo đơn hàng.");
            return;
        }

        runningCall = call;

        runningCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call,
                                   @NonNull Response<Order> response) {

                if (!response.isSuccessful()) {
                    loading.setValue(false);
                    errorMessage.setValue("Tạo đơn thất bại (" + response.code() + ").");
                    return;
                }

                Order order = response.body();
                String orderId = order == null ? "" : safe(order.getId());

                if (orderId.isEmpty()) {
                    loading.setValue(false);
                    errorMessage.setValue("Không nhận được mã đơn hàng.");
                    return;
                }

                submitItemsSequential(orderId, items, 0);
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(msgOf(t));
            }
        });
    }

    private void submitItemsSequential(@NonNull String orderId,
                                       @NonNull List<CartItem> items,
                                       int index) {

        if (index >= items.size()) {
            loading.setValue(false);
            clearCart();
            return;
        }

        CartItem it = items.get(index);

        Call<Order> addCall = orderRepository.addItemToOrder(
                orderId,
                it.foodId,
                it.quantity,
                it.note
        );

        if (addCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể thêm món vào đơn.");
            return;
        }

        runningCall = addCall;

        addCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call,
                                   @NonNull Response<Order> response) {

                if (!response.isSuccessful()) {
                    loading.setValue(false);
                    errorMessage.setValue("Thêm món thất bại (" + response.code() + ").");
                    return;
                }

                submitItemsSequential(orderId, items, index + 1);
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(msgOf(t));
            }
        });
    }

    private void cancelRunningCall() {
        if (runningCall != null) {
            runningCall.cancel();
            runningCall = null;
        }
    }

    @Override
    protected void onCleared() {
        cancelRunningCall();
        super.onCleared();
    }

    private String msgOf(Throwable t) {
        String m = t == null ? null : t.getMessage();
        return (m == null || m.trim().isEmpty())
                ? "Lỗi kết nối. Vui lòng thử lại."
                : m;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    public static class CartItem {
        public final String foodId;
        public final String name;
        public final double unitPrice;
        public final int quantity;
        public final String note;

        public CartItem(String foodId, String name, double unitPrice, int quantity, String note) {
            this.foodId = foodId;
            this.name = name == null ? "" : name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.note = note == null ? "" : note;
        }

        public CartItem withQuantity(int q) {
            return new CartItem(foodId, name, unitPrice, q, note);
        }

        public CartItem withNote(String n) {
            return new CartItem(foodId, name, unitPrice, quantity, n);
        }
    }
}
