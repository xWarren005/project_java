package com.example.s2o_mobile.data.repository;

import androidx.annotation.Nullable;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.RestaurantApi;
import com.example.s2o_mobile.data.repository.RepositoryCallback;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        this.restaurantApi = ApiService.getInstance().getRestaurantApi();
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    @Nullable
    public Call<List<Restaurant>> getRestaurants(Integer page, Integer size) {
        return restaurantApi.getRestaurants(page, size);
    }

    @Nullable
    public Call<List<Restaurant>> searchRestaurants(String keyword, Integer page, Integer size) {
        String q = keyword == null ? "" : keyword.trim();
        if (q.isEmpty()) return null;
        return restaurantApi.searchRestaurants(q, page, size);
    }

    @Nullable
    public Call<Restaurant> getRestaurantDetail(int restaurantId) {
        if (restaurantId <= 0) return null;
        return restaurantApi.getRestaurantDetail(restaurantId);
    }

    @Nullable
    public Call<List<Restaurant>> getFeaturedRestaurants(Integer limit) {
        return restaurantApi.getFeaturedRestaurants(limit);
    }

    @Nullable
    public Call<List<Restaurant>> getRecommendedRestaurants(Integer limit) {
        return restaurantApi.getRecommendedRestaurants(limit);
    }

    public void getAllRestaurants(RepositoryCallback<List<Restaurant>> callback) {
        Call<List<Restaurant>> call = getRestaurants(1, 200);
        if (call == null) {
            if (callback != null) callback.onError("Khong the tai nha hang.");
            return;
        }

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (callback == null) return;
                if (!response.isSuccessful()) {
                    callback.onError("Tai du lieu that bai (" + response.code() + ").");
                    return;
                }
                List<Restaurant> body = response.body();
                callback.onSuccess(body == null ? Collections.emptyList() : body);
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                if (callback == null) return;
                String msg = t == null ? null : t.getMessage();
                callback.onError(msg == null ? "Loi ket noi. Vui long thu lai." : msg);
            }
        });
    }
}
