package com.example.s2o_mobile.ui.order.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.OrderApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuViewModel extends ViewModel {

    private final OrderApi orderApi = ApiService.getOrderApi();
    private final Gson gson = new Gson();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<MenuItem>> menuItems =
            new MutableLiveData<>(new ArrayList<>());

    private int currentRestaurantId = -1;

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public void loadMenu(int restaurantId) {
        if (restaurantId <= 0) {
            errorMessage.setValue("restaurantId không hợp lệ");
            return;
        }

        currentRestaurantId = restaurantId;

        Call<ResponseBody> call = orderApi.getMenu(restaurantId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading.setValue(false);

                if (!response.isSuccessful() || response.body() == null) {
                    errorMessage.setValue("Không lấy được menu");
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}
