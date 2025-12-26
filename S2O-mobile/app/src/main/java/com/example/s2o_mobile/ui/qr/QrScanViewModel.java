package com.example.s2o_mobile.ui.qr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QrScanViewModel extends ViewModel {

    public static class QrResult {
        public final boolean success;
        public final String raw;
        public final Integer restaurantId;
        public final Integer tableId;
        public final String error;

        private QrResult(boolean success, String raw,
                         Integer restaurantId, Integer tableId, String error) {
            this.success = success;
            this.raw = raw;
            this.restaurantId = restaurantId;
            this.tableId = tableId;
            this.error = error;
        }

        public static QrResult ok(String raw, Integer restaurantId, Integer tableId) {
            return new QrResult(true, raw, restaurantId, tableId, null);
        }

        public static QrResult fail(String raw, String error) {
            return new QrResult(false, raw, null, null, error);
        }
    }

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<QrResult> result = new MutableLiveData<>(null);

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<QrResult> getResult() {
        return result;
    }
}
public void onQrScanned(String raw) {
    loading.setValue(true);
    error.setValue(null);

    QrResult parsed = parseQr(raw);

    loading.setValue(false);

    if (parsed.success) {
        result.setValue(parsed);
    } else {
        error.setValue(parsed.error);
        result.setValue(parsed);
    }
}


