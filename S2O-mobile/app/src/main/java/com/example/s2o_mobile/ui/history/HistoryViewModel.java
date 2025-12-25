package com.example.s2o_mobile.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.s2o_mobile.data.repository.HistoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<List<Object>> rows =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Double> totalSpent =
            new MutableLiveData<>(0.0);

    private final MutableLiveData<Integer> totalCount =
            new MutableLiveData<>(0);

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Object>> getRows() {
        return rows;
    }

    public LiveData<Double> getTotalSpent() {
        return totalSpent;
    }

    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }

    public void loadHistory() {
        loading.setValue(true);
        errorMessage.setValue(null);
    }
}

public void loadHistory() {
    loading.setValue(false);
    errorMessage.setValue(null);

    List<Object> result = new ArrayList<>();

    List<Object> bookingHistory = repository.getBookingHistory();
    List<Object> spendingHistory = repository.getSpendingHistory();

    if (bookingHistory != null) {
        result.addAll(bookingHistory);
    }

    if (spendingHistory != null) {
        result.addAll(spendingHistory);
    }

    rows.setValue(result);
    totalCount.setValue(result.size());
    totalSpent.setValue(0.0);
}

