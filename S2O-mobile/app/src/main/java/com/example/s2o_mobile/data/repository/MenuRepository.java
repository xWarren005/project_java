package com.example.s2o_mobile.data.repository;

import androidx.annotation.Nullable;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class MenuRepository {

    private final OrderApi orderApi;

    public MenuRepository() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
    }

    @Nullable
    public Call<List<MenuItem>> getMenu(int restaurantId) {
        if (restaurantId <= 0) return null;
        return orderApi.getMenu(restaurantId);
    }
}
