package com.example.s2o_mobile.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private final RestaurantRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<List<Restaurant>> results =
            new MutableLiveData<>(new ArrayList<>());

    public SearchViewModel(RestaurantRepository repository) {
        this.repository = repository;
    }

    public SearchViewModel() {
        this.repository = new RestaurantRepository();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Restaurant>> getResults() {
        return results;
    }
}
