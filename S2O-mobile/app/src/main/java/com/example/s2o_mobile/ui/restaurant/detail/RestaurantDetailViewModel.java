package com.example.s2o_mobile.ui.restaurant.detail;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;

public class RestaurantDetailViewModel extends ViewModel {

    public MutableLiveData<Boolean> loading;
    public MutableLiveData<String> errorMessage;
    public MutableLiveData<Restaurant> restaurant;

    public RestaurantDetailViewModel() {
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurant;
    }
}
