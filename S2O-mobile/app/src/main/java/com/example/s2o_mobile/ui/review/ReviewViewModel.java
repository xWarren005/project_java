package com.example.s2o_mobile.ui.review;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Review;
import com.example.s2o_mobile.data.repository.ReviewRepository;
import com.example.s2o_mobile.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ReviewViewModel extends AndroidViewModel {

    private final ReviewRepository reviewRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<List<Review>> reviews = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>(false);

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewRepository = ReviewRepository.getInstance(new SessionManager(application));
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> getActionResult() {
        return actionResult;
    }

    public void loadReviews(int restaurantId) {
        if (restaurantId <= 0) {
            errorMessage.setValue("restaurantId không hợp lệ");
            return;
        }

        errorMessage.setValue(null);

        reviewRepository.getReviewsByRestaurant(
                restaurantId,
                reviews,
                errorMessage,
                loading
        );
    }

    public void addReview(int restaurantId, int rating, String content) {
        if (restaurantId <= 0 || content == null) {
            errorMessage.setValue("Dữ liệu không hợp lệ");
            return;
        }

        actionResult.setValue(false);

        reviewRepository.addReview(
                restaurantId,
                rating,
                content.trim(),
                actionResult,
                errorMessage,
                loading
        );
    }

    public void updateReview(int reviewId, int rating, String content) {
        if (reviewId <= 0) {
            errorMessage.setValue("reviewId không hợp lệ");
            return;
        }

        actionResult.setValue(false);

        reviewRepository.updateReview(
                reviewId,
                rating,
                content.trim(),
                actionResult,
                errorMessage,
                loading
        );
    }

    public void deleteReview(int reviewId) {
        if (reviewId <= 0) {
            errorMessage.setValue("reviewId không hợp lệ");
            return;
        }

        actionResult.setValue(false);

        reviewRepository.deleteReview(
                reviewId,
                actionResult,
                errorMessage,
                loading
        );
    }
}
