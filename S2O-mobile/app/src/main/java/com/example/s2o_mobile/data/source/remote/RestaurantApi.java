package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestaurantApi {
    @GET("restaurant")
    Call<List<Restaurant>> getRestaurants(
            @Query("page") int page,
            @Query("size") int size
    );
    @GET("restaurants/search")
    Call<List<Restaurant>> searchRestaurants(
            @Query("keyword") String keyword
    );
    @GET("restaurants/detail")
    Call<Restaurant> getRestaurantDetail(
            @Query("id") String restaurantId
    );
    @GET("restaurants/featured")
    Call<Restaurant> getFeaturedRestaurants();
    @GET("restaurants/recommended")
    Call<Restaurant> getRecommendedRestaurants();
}
