package com.example.s2o_mobile.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.HistoryRow;

import java.util.Collections;
import java.util.List;
public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Double> totalSpent = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> totalCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<HistoryRow>> rows = new MutableLiveData<>(Collections.emptyList());

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<HistoryRow>> getRows() {
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
        rows.setValue(Collections.emptyList());
        totalCount.setValue(0);
        totalSpent.setValue(0.0);
        loading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

