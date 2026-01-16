package com.example.s2o_mobile.data.source.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.s2o_mobile.data.source.remote.RestaurantApi;

public class ApiService {

    private static final String BASE_URL = "http://10.0.2.2:3001/";
    private static ApiService instance;
    private final Retrofit retrofit;

    private ApiService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public AuthApi getAuthApi() {
        return retrofit.create(AuthApi.class);
    }


    public RestaurantApi getRestaurantApi() {
        return retrofit.create(RestaurantApi.class);
    }

    public BookingApi getBookingApi() {
        return retrofit.create(BookingApi.class);
    }

    public ReviewApi getReviewApi() {
        return retrofit.create(ReviewApi.class);
    }

}
