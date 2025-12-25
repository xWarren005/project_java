package com.example.s2o_mobile.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

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
