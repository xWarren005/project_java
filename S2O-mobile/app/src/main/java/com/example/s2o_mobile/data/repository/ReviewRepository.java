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

    public Call<List<Review>> getReviewsByRestaurant(
            @NonNull String restaurantId,
            Integer page,
            Integer size
    ) {
        return reviewApi.getReviewsByRestaurant(restaurantId, page, size);
    }

    public Call<Review> addReview(
            @NonNull String restaurantId,
            @NonNull Review review
    ) {
        return reviewApi.addReview(restaurantId, review);
    }

    public Call<Review> updateReview(
            @NonNull String reviewId,
            @NonNull Review review
    ) {
        return reviewApi.updateReview(reviewId, review);
    }

    public Call<Void> deleteReview(@NonNull String reviewId) {
        return reviewApi.deleteReview(reviewId);
    }
}
