package com.example.s2o_mobile.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;

public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private Call<?> runningCall;

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    protected void setRunningCall(Call<?> call) {
        cancelRunningCall();
        runningCall = call;
    }

    protected void cancelRunningCall() {
        if (runningCall != null) {
            runningCall.cancel();
            runningCall = null;
        }
    }

    protected void postError(Throwable t) {
        if (t == null || t.getMessage() == null || t.getMessage().trim().isEmpty()) {
            errorMessage.postValue("Lỗi kết nối. Vui lòng thử lại.");
        } else {
            errorMessage.postValue(t.getMessage());
        }
    }

    @Override
    protected void onCleared() {
        cancelRunningCall();
        super.onCleared();
    }
}
