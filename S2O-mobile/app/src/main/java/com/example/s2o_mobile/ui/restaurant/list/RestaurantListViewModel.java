package com.example.s2o_mobile.ui.restaurant.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class RestaurantListViewModel extends ViewModel {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private int currentPage = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private boolean hasMore = true;
    private boolean isSearching = false;
    private String currentQuery = "";

    private Call<List<Restaurant>> runningCall;

    public RestaurantListViewModel() {
        this.restaurantRepository = new RestaurantRepository();
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public boolean canLoadMore() {
        Boolean isLoading = loading.getValue();
        return hasMore && (isLoading != null && isLoading);
    }

    public void loadFirstPage() {
        currentPage = 1;
        fetchPage(true);
    }

    public void loadNextPage() {
        if (!canLoadMore()) return;
        fetchPage(false);
    }

    public void search(@NonNull String query) {
        currentQuery = query.trim();
        isSearching = !currentQuery.isEmpty();
        loadFirstPage();
    }

    public void clearSearch() {
        currentQuery = "";
        loadFirstPage();
    }

    private void fetchPage(boolean replace) {
    }
}
