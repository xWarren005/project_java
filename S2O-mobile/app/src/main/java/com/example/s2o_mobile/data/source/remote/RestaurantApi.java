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
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    @GET("restaurants/search")
    Call<List<Restaurant>> searchRestaurants(
            @Query("q") String keyword,
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    @GET("restaurants/{ID}")
    Call<Restaurant> getRestaurantDetail(
            @Path("id") String restaurantId
    );
    @GET("restaurants/featured")
    Call<List<Restaurant>> getFeaturedRestaurants(
            @Query("limit") Integer limit
    );
    @GET("restaurants/recommended")
    Call<List<Restaurant>> getRecommendedRestaurants(
            @Query("limit") Integer limit
    );
}
