package com.example.s2o_mobile.ui.order.detail;

import com.example.s2o_mobile.data.repository.OrderRepository;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import java.util.ArrayList;
import java.text.DecimalFormat;
public class OrderDetailActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected ProgressBar progress;
    protected TextView txtError;

    protected TextView txtOrderId;
    protected TextView txtStatus;
    protected TextView txtTime;
    protected TextView txtTotal;

    protected int orderId;

    protected OrderItemAdapter adapter;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getIntExtra("orderId", -1);

        bindViews();
        setupRecycler();
    }

    protected void setupRecycler() {
        adapter = new OrderItemAdapter(new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
