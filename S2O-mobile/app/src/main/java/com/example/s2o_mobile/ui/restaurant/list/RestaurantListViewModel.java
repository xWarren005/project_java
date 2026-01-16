package com.example.s2o_mobile.ui.restaurant.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantListViewModel extends ViewModel {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<List<Restaurant>> restaurants =
            new MutableLiveData<>(Collections.emptyList());
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

    public RestaurantListViewModel(@NonNull RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
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

    public LiveData<String> getError() {
        return errorMessage;
    }

    public void loadRestaurants() {
        loadFirstPage();
    }

    public boolean canLoadMore() {
        Boolean isLoading = loading.getValue();
        return hasMore && (isLoading == null || !isLoading);
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) return;
        this.pageSize = pageSize;
    }

    public void loadFirstPage() {
        currentPage = 1;
        hasMore = true;
        fetchPage(true);
    }

    public void loadNextPage() {
        if (!canLoadMore()) return;
        currentPage += 1;
        fetchPage(false);
    }

    public void refresh() {
        loadFirstPage();
    }

    public void search(@NonNull String query) {
        currentQuery = query.trim();
        isSearching = !currentQuery.isEmpty();
        loadFirstPage();
    }

    public void clearSearch() {
        currentQuery = "";
        isSearching = false;
        loadFirstPage();
    }

    private void fetchPage(boolean replace) {
        cancelRunningCall();

        loading.setValue(true);
        errorMessage.setValue(null);

        if (isSearching) {
            runningCall = restaurantRepository.searchRestaurants(currentQuery, currentPage, pageSize);
        } else {
            runningCall = restaurantRepository.getRestaurants(currentPage, pageSize);
        }

        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tạo request. Vui lòng kiểm tra cấu hình API");
            return;
        }

        runningCall.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Restaurant>> call,
                                   @NonNull Response<List<Restaurant>> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Tải dữ liệu thất bại (" + response.code() + ").");
                    return;
                }

                List<Restaurant> body = response.body();
                List<Restaurant> newItems = body == null ? Collections.emptyList() : body;

                hasMore = newItems.size() >= pageSize;

                if (replace) {
                    restaurants.setValue(new ArrayList<>(newItems));
                } else {
                    List<Restaurant> current = restaurants.getValue();
                    List<Restaurant> merged = new ArrayList<>(
                            current == null ? Collections.emptyList() : current
                    );
                    merged.addAll(newItems);
                    restaurants.setValue(merged);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Restaurant>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(
                        t.getMessage() == null ? "Lỗi kết nối. Vui lòng thử lại." : t.getMessage()
                );
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
}
