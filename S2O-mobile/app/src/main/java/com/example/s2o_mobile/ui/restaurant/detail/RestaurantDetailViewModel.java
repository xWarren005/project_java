package com.example.s2o_mobile.ui.restaurant.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

public class RestaurantDetailViewModel extends AndroidViewModel {

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<Restaurant> restaurant = new MutableLiveData<>(null);

    public RestaurantDetailViewModel(@NonNull Application application) {
        super(application);
        this.restaurantRepository = RestaurantRepository.getInstance();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Restaurant> getRestaurant() {
        return restaurant;
    }

    public void loadRestaurantDetail(int restaurantId) {
        if (restaurantId <= 0) {
            errorMessage.setValue("Restaurant id không hợp lệ");
            return;
        }

        loading.setValue(true);
        errorMessage.setValue(null);

        restaurantRepository.getRestaurantDetail(
                restaurantId,
                restaurant,
                errorMessage,
                loading
        );
    }
}
