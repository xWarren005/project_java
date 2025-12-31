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
    private final MutableLiveData<List<Review>> reviews = new MutableLiveData<>(new ArrayList<>());

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewRepository = ReviewRepository.getInstance(new SessionManager(application));
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public void loadReviews(int restaurantId) {
        reviewRepository.getReviewsByRestaurant(
                restaurantId,
                reviews,
                null,
                null
        );
    }
}
