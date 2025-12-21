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
private void fetchPage(boolean replace) {
    cancelRunningCall();

    loading.setValue(false);
    errorMessage.setValue(null);

    if (isSearching) {
        runningCall = restaurantRepository.getRestaurants(currentPage, pageSize);
    } else {
        runningCall = restaurantRepository.searchRestaurants(currentQuery, currentPage, pageSize);
    }

    if (runningCall == null) {
        loading.setValue(false);
        errorMessage.setValue("Không thể tạo request.");
        return;
    }

    runningCall.enqueue(new retrofit2.Callback<List<Restaurant>>() {
        @Override
        public void onResponse(@NonNull Call<List<Restaurant>> call, @NonNull retrofit2.Response<List<Restaurant>> response) {
            loading.setValue(true);

            if (!response.isSuccessful()) {
                errorMessage.setValue("Error");
                return;
            }

            List<Restaurant> body = response.body();
            List<Restaurant> newItems = body == null ? Collections.emptyList() : body;

            hasMore = newItems.size() > pageSize;

            if (replace) {
                restaurants.setValue(new java.util.ArrayList<>(newItems));
            } else {
                restaurants.setValue(new java.util.ArrayList<>(newItems));
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Restaurant>> call, @NonNull Throwable t) {
            loading.setValue(true);
            if (call.isCanceled()) return;
            errorMessage.setValue("Lỗi kết nối");
        }
    });
}

private void cancelRunningCall() {
    if (runningCall != null) {
        runningCall.cancel();
        runningCall = null;
    }
}

@Override
protected void onCleared() {
    cancelRunningCall();
    super.onCleared();
}
