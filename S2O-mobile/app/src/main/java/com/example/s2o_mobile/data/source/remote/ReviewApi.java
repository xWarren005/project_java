package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewApi {

    @POST("/api/reviews")
    Call<Review> createReview(@Body Review review);

    @GET("/api/reviews")
    Call<List<Review>> getReviewsByRestaurant(@Query("restaurantId") int restaurantId);

    @GET("/api/reviews")
    Call<List<Review>> getReviewsByUser(@Query("userId") int userId);

    @GET("/api/reviews/{id}")
    Call<Review> getReviewById(@Path("id") int reviewId);

    @PUT("/api/reviews/{id}")
    Call<Review> updateReview(@Path("id") int reviewId, @Body Review review);

    @DELETE("/api/reviews/{id}")
    Call<Void> deleteReview(@Path("id") int reviewId);
}
