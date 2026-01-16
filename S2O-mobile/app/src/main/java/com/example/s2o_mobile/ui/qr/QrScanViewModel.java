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

    private QrResult parseQr(String raw) {
        if (raw == null) return QrResult.fail("", "QR rong");
        String s = raw.trim();
        if (s.isEmpty()) return QrResult.fail(raw, "QR rong");

        if (isAllDigits(s)) {
            return QrResult.ok(raw, null, toIntSafe(s));
        }

        int q = s.indexOf('?');
        if (q >= 0 && q < s.length() - 1) {
            s = s.substring(q + 1);
        }

        s = s.replace(";", "&");

        Integer restaurantId = null;
        Integer tableId = null;

        String[] parts = s.split("&");
        for (String p : parts) {
            if (p == null) continue;
            String[] kv = p.split("=");
            if (kv.length != 2) continue;

            String key = kv[0].trim().toLowerCase();
            String val = kv[1].trim();

            if (key.equals("restaurantid") || key.equals("restaurant_id")) {
                restaurantId = toIntSafe(val);
            } else if (key.equals("tableid") || key.equals("table_id")) {
                tableId = toIntSafe(val);
            }
        }

        if (restaurantId == null && tableId == null) {
            return QrResult.fail(raw, "QR khong dung dinh dang");
        }

        return QrResult.ok(raw, restaurantId, tableId);
    }

    private boolean isAllDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    private Integer toIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
