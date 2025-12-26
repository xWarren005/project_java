package com.example.s2o_mobile.ui.order.qr;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;

public class QrScanActivity extends BaseActivity {

    private View btnScan;
    private View progress;

    private final ActivityResultLauncher<String> scanLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                progress.setVisibility(View.GONE);
                Toast.makeText(this, "Đã quét QR", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        btnScan = findViewById(R.id.btnScan);
        progress = findViewById(R.id.progress);

        progress.setVisibility(View.GONE);

        btnScan.setOnClickListener(v -> startScan());
    }

    private void startScan() {
        progress.setVisibility(View.VISIBLE);
        scanLauncher.launch("image/*");
    }
}
