package com.example.s2o_mobile.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.repository.MenuRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuViewModel extends ViewModel {

    private final MenuRepository menuRepository;

    private final MutableLiveData<List<MenuItem>> menuItems =
            new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);
    private final MutableLiveData<String> error =
            new MutableLiveData<>(null);

    private Call<List<MenuItem>> runningCall;

    public MenuViewModel() {
        menuRepository = new MenuRepository();
    }

    public MenuViewModel(@NonNull MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMenu(int restaurantId) {
        cancelRunningCall();
        loading.setValue(true);
        error.setValue(null);

        Call<List<MenuItem>> call = menuRepository.getMenu(restaurantId);
        if (call == null) {
            loading.setValue(false);
            error.setValue("Khong the tai menu.");
            return;
        }

        runningCall = call;

        call.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuItem>> call,
                                   @NonNull Response<List<MenuItem>> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    error.setValue("Tai menu that bai (" + response.code() + ").");
                    return;
                }

                List<MenuItem> body = response.body();
                menuItems.setValue(body == null ? Collections.emptyList() : body);
            }

            @Override
            public void onFailure(@NonNull Call<List<MenuItem>> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                String msg = t.getMessage();
                error.setValue(msg == null ? "Loi ket noi. Vui long thu lai." : msg);
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
