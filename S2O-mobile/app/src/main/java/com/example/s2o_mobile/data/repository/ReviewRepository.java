package com.example.s2o_mobile.data.repository;

import androidx.annotation.NonNull;

import com.example.s2o_mobile.data.model.Review;
import com.example.s2o_mobile.data.source.remote.ReviewApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class ReviewRepository {

    private final ReviewApi reviewApi;

    public ReviewRepository() {
        reviewApi = RetrofitClient
                .getInstance()
                .create(ReviewApi.class);
    }

    public ReviewRepository(@NonNull ReviewApi reviewApi) {
        this.reviewApi = reviewApi;
    }

    public Call<List<Review>> getReviewsByRestaurant(int restaurantId) {
        return reviewApi.getReviewsByRestaurant(restaurantId);
    }

    public Call<Review> addReview(@NonNull Review review) {
        return reviewApi.createReview(review);
    }

    public Call<Review> updateReview(int reviewId, @NonNull Review review) {
        return reviewApi.updateReview(reviewId, review);
    }

    public Call<Void> deleteReview(int reviewId) {
        return reviewApi.deleteReview(reviewId);
    }
}
