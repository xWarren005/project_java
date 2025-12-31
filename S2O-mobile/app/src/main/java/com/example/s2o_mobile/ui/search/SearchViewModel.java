package com.example.s2o_mobile.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RepositoryCallback;
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

    public void search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            results.setValue(new ArrayList<>());
            return;
        }

        loading.setValue(true);
        error.setValue(null);

        repository.getAllRestaurants(new RepositoryCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> data) {
                loading.postValue(false);

                List<Restaurant> filtered = new ArrayList<>();
                for (Restaurant r : data) {
                    if (r == null) continue;

                    String name = safe(r.getName());
                    String address = safe(r.getAddress());

                    if (name.toLowerCase().contains(keyword.toLowerCase())
                            || address.toLowerCase().contains(keyword.toLowerCase())) {
                        filtered.add(r);
                    }
                }

                results.postValue(filtered);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message == null ? "Loi tim kiem" : message);
            }
        });
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
