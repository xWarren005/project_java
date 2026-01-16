package com.example.s2o_mobile.ui.review;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Review;
import com.example.s2o_mobile.data.repository.ReviewRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewViewModel extends ViewModel {

    private final ReviewRepository reviewRepository;

    private final MutableLiveData<List<Review>> reviews =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>(null);

    private Call<List<Review>> runningCall;

    public ReviewViewModel() {
        reviewRepository = new ReviewRepository();
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadReviews(int restaurantId) {
        if (restaurantId <= 0) return;

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = reviewRepository.getReviewsByRestaurant(restaurantId);

        runningCall.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(@NonNull Call<List<Review>> call,
                                   @NonNull Response<List<Review>> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Tải đánh giá thất bại");
                    return;
                }

                List<Review> body = response.body();
                reviews.setValue(body == null ? Collections.emptyList() : body);
            }

            @Override
            public void onFailure(@NonNull Call<List<Review>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(
                        t.getMessage() == null ? "Lỗi kết nối" : t.getMessage()
                );
            }
        });
    }

    public void addReview(@NonNull Review review) {
        if (review == null) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Review> call = reviewRepository.addReview(review);

        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(@NonNull Call<Review> call,
                                   @NonNull Response<Review> response) {
                loading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Review> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Không thể gửi đánh giá");
            }
        });
    }

    public void updateReview(int reviewId, @NonNull Review review) {
        if (reviewId <= 0 || review == null) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Review> call = reviewRepository.updateReview(reviewId, review);

        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(@NonNull Call<Review> call,
                                   @NonNull Response<Review> response) {
                loading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Review> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Không thể cập nhật đánh giá");
            }
        });
    }

    public void deleteReview(int reviewId) {
        if (reviewId <= 0) return;

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Void> call = reviewRepository.deleteReview(reviewId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                loading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Không thể xoá đánh giá");
            }
        });
    }

    private void cancelRunningCall() {
        if (runningCall != null) {
            runningCall.cancel();
            runningCall = null;
        }
    }

    @Override
    protected void onCleared() {
        cancelRunningCall();
        super.onCleared();
    }
}
