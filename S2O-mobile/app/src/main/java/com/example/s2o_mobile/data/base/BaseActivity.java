package com.example.s2o_mobile.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Đang xử lý...");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    protected void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showToast(String message) {
        if (message == null || message.trim().isEmpty()) return;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void finishSafe() {
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        super.onDestroy();
    }
}
