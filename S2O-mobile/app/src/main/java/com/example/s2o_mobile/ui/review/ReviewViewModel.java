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
}
