package com.example.s2o_mobile.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    // Danh sách nhà hàng cho Home
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>(new ArrayList<>());

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.restaurantRepository = RestaurantRepository.getInstance();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }

    public void loadHomeRestaurants() {
        loading.setValue(true);
        errorMessage.setValue(null);

        restaurantRepository.getRestaurants(
                restaurants,
                errorMessage,
                loading
        );
    }

    public void loadRecommendedRestaurants() {
        loading.setValue(true);
        errorMessage.setValue(null);

        restaurantRepository.getRecommendedRestaurants(
                restaurants,
                errorMessage,
                loading
        );
    }
}
