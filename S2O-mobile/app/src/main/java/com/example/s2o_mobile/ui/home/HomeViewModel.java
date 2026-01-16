package com.example.s2o_mobile.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<List<Restaurant>> featuredRestaurants =
            new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<Restaurant>> recommendedRestaurants =
            new MutableLiveData<>(Collections.emptyList());

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.restaurantRepository = RestaurantRepository.getInstance();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Restaurant>> getFeaturedRestaurants() {
        return featuredRestaurants;
    }

    public LiveData<List<Restaurant>> getRecommendedRestaurants() {
        return recommendedRestaurants;
    }

    public void loadHomeData() {
        loading.setValue(true);
        errorMessage.setValue(null);

        loadFeatured();
        loadRecommended();
    }

    private void loadFeatured() {
        Call<List<Restaurant>> call = restaurantRepository.getFeaturedRestaurants(10);
        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải danh sách nổi bật");
            return;
        }

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Restaurant>> call,
                                   @NonNull Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    featuredRestaurants.setValue(response.body());
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Restaurant>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue(t.getMessage() == null ? "Lỗi kết nối" : t.getMessage());
            }
        });
    }

    private void loadRecommended() {
        Call<List<Restaurant>> call = restaurantRepository.getRecommendedRestaurants(10);
        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải danh sách gợi ý");
            return;
        }

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(@NonNull Call<List<Restaurant>> call,
                                   @NonNull Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedRestaurants.setValue(response.body());
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Restaurant>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                errorMessage.setValue(t.getMessage() == null ? "Lỗi kết nối" : t.getMessage());
            }
        });
    }
}
