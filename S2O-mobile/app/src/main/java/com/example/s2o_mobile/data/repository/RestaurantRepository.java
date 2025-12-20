package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.RestaurantApi;

public class RestaurantRepository {

    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        this.restaurantApi = ApiService.getInstance().getRestaurantApi();
    }
}
