package com.example.s2o_mobile.ui.restaurant.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailViewModel extends AndroidViewModel {

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<Restaurant> restaurant = new MutableLiveData<>(null);

    public RestaurantDetailViewModel(@NonNull Application application) {
        super(application);
        this.restaurantRepository = RestaurantRepository.getInstance();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Restaurant> getRestaurant() {
        return restaurant;
    }

    public void loadRestaurantDetail(int restaurantId) {
        if (restaurantId <= 0) {
            errorMessage.setValue("Restaurant id không hợp lệ");
            return;
        }

        loading.setValue(true);
        errorMessage.setValue(null);

        Call<Restaurant> call = restaurantRepository.getRestaurantDetail(restaurantId);
        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải dữ liệu nhà hàng");
            return;
        }

        call.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    restaurant.setValue(response.body());
                } else {
                    errorMessage.setValue("Không tìm thấy nhà hàng");
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue(t.getMessage() == null ? "Lỗi kết nối" : t.getMessage());
            }
        });
    }
}
