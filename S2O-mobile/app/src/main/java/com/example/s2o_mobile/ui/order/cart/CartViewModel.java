package com.example.s2o_mobile.ui.order.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.CartItem;
import com.example.s2o_mobile.data.repository.CartRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository;

    private final MutableLiveData<List<CartItem>> cartItems =
            new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Double> totalPrice =
            new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);
    private final MutableLiveData<String> error =
            new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> checkoutSuccess =
            new MutableLiveData<>(false);

    public CartViewModel() {
        this.cartRepository = new CartRepository();
    }

    public CartViewModel(@NonNull CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getCheckoutSuccess() {
        return checkoutSuccess;
    }

    public void loadCart() {
        List<CartItem> items = cartRepository.getItems();
        cartItems.setValue(items);
        recalcTotal(items);
    }

    public void increaseQty(@NonNull CartItem item) {
        updateQty(item, item.getQuantity() + 1);
    }

    public void decreaseQty(@NonNull CartItem item) {
        updateQty(item, item.getQuantity() - 1);
    }

    public void removeItem(@NonNull CartItem item) {
        List<CartItem> list = snapshot();
        list.remove(item);
        updateCart(list);
    }

    public void checkout() {
        loading.setValue(true);
        error.setValue(null);

        List<CartItem> list = snapshot();
        if (list.isEmpty()) {
            loading.setValue(false);
            error.setValue("Gio hang dang trong.");
            return;
        }

        cartRepository.setItems(new ArrayList<>());
        cartItems.setValue(Collections.emptyList());
        totalPrice.setValue(0.0);
        loading.setValue(false);
        checkoutSuccess.setValue(true);
    }

    private void updateQty(@NonNull CartItem item, int newQty) {
        List<CartItem> list = snapshot();
        for (CartItem it : list) {
            if (it == item || sameId(it, item)) {
                if (newQty <= 0) {
                    list.remove(it);
                } else {
                    it.setQuantity(newQty);
                }
                break;
            }
        }
        updateCart(list);
    }

    private boolean sameId(CartItem a, CartItem b) {
        if (a == null || b == null) return false;
        String ida = a.getId();
        String idb = b.getId();
        return ida != null && ida.equals(idb);
    }

    private List<CartItem> snapshot() {
        List<CartItem> list = cartItems.getValue();
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    private void updateCart(List<CartItem> list) {
        cartRepository.setItems(list);
        cartItems.setValue(list == null ? Collections.emptyList() : list);
        recalcTotal(list);
    }

    private void recalcTotal(List<CartItem> list) {
        double sum = 0.0;
        if (list != null) {
            for (CartItem it : list) {
                if (it == null) continue;
                sum += it.getUnitPrice() * Math.max(0, it.getQuantity());
            }
        }
        totalPrice.setValue(sum);
    }
}
