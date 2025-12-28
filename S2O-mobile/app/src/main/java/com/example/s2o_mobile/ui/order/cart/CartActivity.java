package com.example.s2o_mobile.ui.order.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.s2o_mobile.R;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected TextView txtEmpty;
    protected TextView txtTotal;
    protected Button btnCheckout;
    protected ProgressBar progress;
    protected CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        bindViews();
    }

    protected void bindViews() {
        recyclerView = findViewById(R.id.recyclerCart);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtTotal = findViewById(R.id.txtTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        progress = findViewById(R.id.progress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        bindViews();
        setupRecycler();
        setupActions();
    }

    protected void setupRecycler() {
        adapter = new CartAdapter(
                new ArrayList<>(),
                item -> {},
                item -> {},
                item -> {}
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    protected void setupActions() {
        btnCheckout.setOnClickListener(v -> {
        });
    }
}
