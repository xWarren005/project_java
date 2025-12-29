package com.example.s2o_mobile.ui.order.detail;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;

public class OrderDetailActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected ProgressBar progress;
    protected TextView txtError;

    protected TextView txtOrderId;
    protected TextView txtStatus;
    protected TextView txtTime;
    protected TextView txtTotal;

    protected int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getIntExtra("orderId", -1);
        bindViews();
    }

    protected void bindViews() {
        recyclerView = findViewById(R.id.recyclerOrderItems);
        progress = findViewById(R.id.progress);
        txtError = findViewById(R.id.txtError);

        txtOrderId = findViewById(R.id.txtOrderId);
        txtStatus = findViewById(R.id.txtStatus);
        txtTime = findViewById(R.id.txtTime);
        txtTotal = findViewById(R.id.txtTotal);
    }
}
