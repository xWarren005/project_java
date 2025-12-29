package com.example.s2o_mobile.ui.order.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.repository.OrderRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView txtError;

    private TextView txtOrderId;
    private TextView txtStatus;
    private TextView txtTime;
    private TextView txtTotal;

    private OrderItemAdapter adapter;
    private OrderDetailViewModel viewModel;

    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getIntExtra("orderId", -1);

        bindViews();
        setupRecycler();
        setupViewModel();
        observeState();

        if (orderId != -1) {
            viewModel.loadOrderDetail(orderId);
        } else {
            showError("Khong tim thay ma don hang");
        }
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerOrderItems);
        progress = findViewById(R.id.progress);
        txtError = findViewById(R.id.txtError);

        txtOrderId = findViewById(R.id.txtOrderId);
        txtStatus = findViewById(R.id.txtStatus);
        txtTime = findViewById(R.id.txtTime);
        txtTotal = findViewById(R.id.txtTotal);
    }

    private void setupRecycler() {
        adapter = new OrderItemAdapter(new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        OrderRepository repo = new OrderRepository();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(OrderDetailViewModel.class)) {
                    return (T) new OrderDetailViewModel(repo);
                }
                throw new IllegalArgumentException("Unknown ViewModel");
            }
        }).get(OrderDetailViewModel.class);
    }

    private void observeState() {
        viewModel.getLoading().observe(this, loading -> {
            boolean isLoading = loading != null && loading;
            progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                showError(err);
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            } else {
                txtError.setVisibility(View.GONE);
                txtError.setText("");
            }
        });

        viewModel.getOrderHeader().observe(this, header -> {
            if (header == null) return;

            txtOrderId.setText("Don #" + safe(header.orderCode));
            txtStatus.setText(safe(header.status));
            txtTime.setText(safe(header.timeText));
            txtTotal.setText(formatMoney(header.totalAmount));
        });

        viewModel.getOrderItems().observe(this, items -> {
            if (items == null || items.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setData(items);
            }
        });
    }

    private void showError(String msg) {
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(msg == null ? "" : msg);
    }

    private String formatMoney(double amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount) + " VND";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
