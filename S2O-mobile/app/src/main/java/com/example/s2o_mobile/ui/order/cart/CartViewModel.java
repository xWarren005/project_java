package com.example.s2o_mobile.ui.order.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private Call<?> runningCall;

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
