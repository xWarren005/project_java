package com.example.s2o_mobile.ui.order.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;

public class CartActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected TextView txtEmpty;
    protected TextView txtTotal;
    protected Button btnCheckout;
    protected ProgressBar progress;

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
}
