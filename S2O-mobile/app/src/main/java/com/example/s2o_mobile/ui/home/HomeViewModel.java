package com.example.s2o_mobile.ui.home;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.repository.RestaurantRepository;

public class HomeViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;

    public HomeViewModel(Application application) {
        restaurantRepository = new RestaurantRepository();
    }
}
