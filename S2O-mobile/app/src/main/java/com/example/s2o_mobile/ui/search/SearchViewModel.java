package com.example.s2o_mobile.ui.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {

    private final RestaurantRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<Restaurant>> results = new MutableLiveData<>(Collections.emptyList());

    private Call<List<Restaurant>> runningCall;

    public SearchViewModel() {
        this.repository = new RestaurantRepository();
    }

    public SearchViewModel(@NonNull RestaurantRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Restaurant>> getResults() {
        return results;
    }

    public void clear() {
        cancelRunningCall();
        results.setValue(Collections.emptyList());
        errorMessage.setValue(null);
        loading.setValue(false);
    }

    public void search(@NonNull String keyword) {
        String q = keyword.trim();
        if (q.isEmpty()) {
            clear();
            return;
        }

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = repository.searchRestaurants(q, 1, 20);
        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tìm kiếm.");
            return;
        }

        runningCall.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Restaurant>> call,
                                   @NonNull Response<List<Restaurant>> response) {
                loading.setValue(false);
                if (!response.isSuccessful()) {
                    errorMessage.setValue("Tìm kiếm thất bại (" + response.code() + ").");
                    return;
                }
                List<Restaurant> body = response.body();
                results.setValue(body == null ? Collections.emptyList() : body);
            }

            @Override
            public void onFailure(@NonNull Call<List<Restaurant>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                String msg = t.getMessage();
                errorMessage.setValue(msg == null ? "Lỗi kết nối. Vui lòng thử lại." : msg);
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
