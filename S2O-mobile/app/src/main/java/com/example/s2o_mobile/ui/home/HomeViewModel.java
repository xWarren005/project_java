package com.example.s2o_mobile.ui.home;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;

    public MutableLiveData<Boolean> loading;
    public MutableLiveData<String> errorMessage;
    public MutableLiveData<List<Restaurant>> restaurants;

    public HomeViewModel(Application application) {
        restaurantRepository = new RestaurantRepository();
    }

    public MutableLiveData getRestaurants() {
        return restaurants;
    }
}
