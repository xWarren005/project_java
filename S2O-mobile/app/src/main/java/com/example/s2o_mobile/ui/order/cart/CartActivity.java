package com.example.s2o_mobile.ui.order.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.repository.CartRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private TextView txtTotal;
    private Button btnCheckout;
    private ProgressBar progress;

    private CartAdapter adapter;
    private CartViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        bindViews();
        setupRecycler();
        setupViewModel();
        observeState();

        viewModel.loadCart();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerCart);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtTotal = findViewById(R.id.txtTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        progress = findViewById(R.id.progress);

        btnCheckout.setOnClickListener(v -> onCheckoutClicked());
    }

    private void setupRecycler() {
        adapter = new CartAdapter(new ArrayList<>(),
                item -> viewModel.increaseQty(item),
                item -> viewModel.decreaseQty(item),
                item -> viewModel.removeItem(item)
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        CartRepository repo = new CartRepository();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(CartViewModel.class)) {
                    return (T) new CartViewModel(repo);
                }
                throw new IllegalArgumentException("Unknown ViewModel");
            }
        }).get(CartViewModel.class);
    }

    private void observeState() {
        viewModel.getLoading().observe(this, loading -> {
            boolean isLoading = loading != null && loading;
            progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnCheckout.setEnabled(!isLoading);
        });

        viewModel.getCartItems().observe(this, items -> {
            boolean empty = items == null || items.isEmpty();

            txtEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
            btnCheckout.setEnabled(!empty);

            if (!empty) {
                adapter.setData(items);
            }
        });

        viewModel.getTotalPrice().observe(this, total -> {
            double value = total == null ? 0.0 : total;
            txtTotal.setText(formatMoney(value));
        });

        viewModel.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCheckoutSuccess().observe(this, ok -> {
            if (ok != null && ok) {
                Toast.makeText(this, "Dat hang thanh cong", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    private void onCheckoutClicked() {
        viewModel.checkout();
    }

    private String formatMoney(double amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount) + " VND";
    }
}
