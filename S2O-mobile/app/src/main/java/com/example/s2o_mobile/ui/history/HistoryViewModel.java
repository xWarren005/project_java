package com.example.s2o_mobile.ui.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.model.HistoryItem;
import com.example.s2o_mobile.data.repository.BookingRepository;
import com.example.s2o_mobile.data.repository.HistoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class HistoryViewModel extends ViewModel {
    private final HistoryRepository repository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<List<HistoryItem>> historyItems = new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Double> totalSpent = new MutableLiveData<>(0.0);

    private final MutableLiveData<Integer> totalCount = new MutableLiveData<>(0);

    private Call<List<Booking>> bookingCall;
    private Call<List<Spending>> spendingCall;

    private List<Booking> cachedBookings;
    private List<Spending> cachedSpendings;

    public HistoryViewModel() {
        this.repository = new HistoryRepository();
    }

    public HistoryViewModel(@NonNull HistoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<HistoryRow>> getRows() {
        return row;
    }

    public LiveData<Double> getTotalSpent() {
        return totalSpent;
    }

    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }

    public void loadHistory() {
        cancelCalls();

        loading.setValue(true);
        errorMessage.setValue(null);

        cachedBookings = null;
        cachedSpendings = null;

        bookingCall = bookingRepository.getBookingHistory();
        spendingCall = historyRepository.getSpendingHistory();

        if (bookingCall == null || spendingCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tạo request. Vui lòng kiểm tra cấu hình repository/API.");
            return;
        }

        bookingCall.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<List<Booking>> call, @NonNull Response<List<Booking>> response) {
                if (!response.isSuccessful()) {
                    finishWithError("Tải lịch sử đặt bàn thất bại (" + response.code() + ").");
                    return;
                }
                List<Booking> body = response.body();
                cachedBookings = body == null ? Collections.emptyList() : body;
                aggregateIfReady();
            }

            public void onFailure(@NonNull Call<List<Booking>> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                finishWithError(messageOf(t));
            }
        });

        spendingCall.enqueue(new Callback<List<Spending>>() {
            @Override
            public void onResponse(@NonNull Call<List<Spending>> call, @NonNull Response<List<Spending>> response) {
                if (!response.isSuccessful()) {
                    finishWithError("Tải lịch sử chi tiêu thất bại (" + response.code() + ").");
                    return;
                }
                List<Spending> body = response.body();
                cachedSpendings = body == null ? Collections.emptyList() : body;
                aggregateIfReady();
            }

            @Override
            public void onFailure(@NonNull Call<List<Spending>> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                finishWithError(messageOf(t));
            }
        });
    }

    private void aggregateIfReady() {
        if (cachedBookings == null || cachedSpendings == null) return;

        List<HistoryRow> merged = new ArrayList<>();

        for (Booking b : cachedBookings) {
            merged.add(HistoryRow.fromBooking(b));
        }

        for (Spending s : cachedSpendings) {
            merged.add(HistoryRow.fromSpending(s));
        }

        merged.sort(new Comparator<HistoryRow>() {
            @Override
            public int compare(HistoryRow o1, HistoryRow o2) {
                long t1 = o1 == null ? 0L : o1.getTimeMillis();
                long t2 = o2 == null ? 0L : o2.getTimeMillis();
                return Long.compare(t2, t1);
            }
        });

        rows.setValue(merged);
        totalCount.setValue(merged.size());
        totalSpent.setValue(sumSpending(merged));

        loading.setValue(false);
    }

    private double sumSpending(List<HistoryRow> list) {
        double sum = 0.0;
        if (list == null) return sum;
        for (HistoryRow r : list) {
            if (r == null) continue;
            Double amount = r.getAmount();
            if (amount != null) sum += amount;
        }
        return sum;
    }

    private void finishWithError(String msg) {
        cancelCalls();
        loading.setValue(false);
        errorMessage.setValue(msg);
    }

    private String messageOf(@Nullable Throwable t) {
        if (t == null) return "Lỗi kết nối. Vui lòng thử lại.";
        String m = t.getMessage();
        if (m == null || m.trim().isEmpty()) return "Lỗi kết nối. Vui lòng thử lại.";
        return m;
    }

    private void cancelCalls() {
        if (bookingCall != null) {
            bookingCall.cancel();
            bookingCall = null;
        }
        if (spendingCall != null) {
            spendingCall.cancel();
            spendingCall = null;
        }
    }

    @Override
    protected void onCleared() {
        cancelCalls();
        super.onCleared();
    }
}

