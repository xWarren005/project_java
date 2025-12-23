package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.RestaurantApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        this.restaurantApi = ApiService.getInstance().getRestaurantApi();
    }

    public void getAllRestaurants(RepositoryCallback<List<Restaurant>> callback) {
        restaurantApi.getAllRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Khong lay duoc danh sach nha hang");
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                callback.onError(t == null ? "Loi khong xac dinh" : t.getMessage());
            }
        });
    }

    public void getRestaurantById(int restaurantId, RepositoryCallback<Restaurant> callback) {
        restaurantApi.getRestaurantById(restaurantId).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Khong tim thay nha hang");
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                callback.onError(t == null ? "Loi khong xac dinh" : t.getMessage());
            }
        });
    }

    public void getRestaurantDetail(int restaurantId, RepositoryCallback<Restaurant> callback) {
        getRestaurantById(restaurantId, callback);
    }
}
