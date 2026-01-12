package com.example.s2o_mobile.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.ui.restaurant.detail.RestaurantDetailActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantAdapter;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantClickListener;

import java.util.Collections;
import java.util.List;

public class RestaurantHistoryActivity extends BaseActivity
        implements RestaurantClickListener {

    private RecyclerView rvRestaurants;
    private View progress;
    private View emptyView;

    private RestaurantAdapter adapter;
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_history);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        viewModel.loadRestaurantHistory();
    }

    private void bindViews() {
        rvRestaurants = findViewById(R.id.rvRestaurants);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, this);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
        rvRestaurants.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(HistoryViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getRestaurantHistory().observe(this, list -> {
            List<Restaurant> safe = list == null
                    ? Collections.emptyList()
                    : list;

            adapter.submitList(safe);
            setVisible(emptyView, safe.isEmpty());
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisible(View view, boolean visible) {
        if (view == null) return;
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRestaurantClick(@NonNull Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(
                RestaurantDetailActivity.EXTRA_RESTAURANT_ID,
                restaurant.getId()
        );
        startActivity(intent);
    }
}
