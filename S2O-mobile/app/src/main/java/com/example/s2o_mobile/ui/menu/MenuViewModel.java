package com.example.s2o_mobile.ui.order.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.MenuItem;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.repository.OrderRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuViewModel extends ViewModel {

    private final OrderRepository orderRepository;

    private final MutableLiveData<List<MenuItem>> menuItems =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>(null);

    private final MutableLiveData<String> actionMessage =
            new MutableLiveData<>(null);

    private Call<?> runningCall;

    public MenuViewModel() {
        orderRepository = new OrderRepository();
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getActionMessage() {
        return actionMessage;
    }

    public void loadMenu(@NonNull String restaurantId) {
        String id = restaurantId.trim();
        if (id.isEmpty()) return;

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        Call<List<MenuItem>> call = orderRepository.getMenu(id);
        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải menu.");
            return;
        }

        runningCall = call;

        call.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuItem>> call,
                                   @NonNull Response<List<MenuItem>> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Tải menu thất bại (" + response.code() + ").");
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
                errorMessage.setValue(msgOf(t));
            }
        });
    }

    public void orderItem(@NonNull String orderId,
                          @NonNull String foodId,
                          int quantity,
                          @Nullable String note) {
        String oid = orderId.trim();
        String fid = foodId.trim();

        if (oid.isEmpty() || fid.isEmpty()) {
            errorMessage.setValue("Thiếu mã đơn hoặc mã món.");
            return;
        }

        if (quantity <= 0) {
            errorMessage.setValue("Số lượng không hợp lệ.");
            return;
        }

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);
        actionMessage.setValue(null);

        Call<Order> call = orderRepository.addItemToOrder(oid, fid, quantity, note == null ? "" : note.trim());
        if (call == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể gọi món.");
            return;
        }

        runningCall = call;

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call,
                                   @NonNull Response<Order> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Gọi món thất bại (" + response.code() + ").");
                    return;
                }

                actionMessage.setValue("Đã thêm món vào đơn.");
            }

            @Override
            public void onFailure(@NonNull Call<Order> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(msgOf(t));
            }
        });
    }

    private void cancelRunningCall() {
        if (runningCall != null) {
            runningCall.cancel();
            runningCall = null;
        }
    }

    private String msgOf(Throwable t) {
        String m = t == null ? null : t.getMessage();
        return (m == null || m.trim().isEmpty()) ? "Lỗi kết nối. Vui lòng thử lại." : m;
    }

    @Override
    protected void onCleared() {
        cancelRunningCall();
        super.onCleared();
    }
}
