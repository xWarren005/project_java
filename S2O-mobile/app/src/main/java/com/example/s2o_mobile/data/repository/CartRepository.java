package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
    }
}