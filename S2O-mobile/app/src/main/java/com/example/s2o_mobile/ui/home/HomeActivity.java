package com.example.s2o_mobile.ui.home;

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

import java.util.Collections;
import java.util.List;

public class HomeActivity extends BaseActivity implements RestaurantClickListener {

    private RecyclerView rvFeatured;
    private RecyclerView rvRecommended;

    private View progress;
    private View emptyFeatured;
    private View emptyRecommended;

    private HomeViewModel viewModel;

    private RestaurantAdapter featuredAdapter;
    private RestaurantAdapter recommendedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViews();
        setupRecyclerViews();
        setupViewModel();
        observeViewModel();
    }

    private void bindViews() {
        rvFeatured = findViewById(R.id.rvFeatured);
        rvRecommended = findViewById(R.id.rvRecommended);

        progress = findViewById(R.id.progress);
        emptyFeatured = findViewById(R.id.emptyFeatured);
        emptyRecommended = findViewById(R.id.emptyFeatured);
    }

    private void setupRecyclerViews() {
        featuredAdapter = new RestaurantAdapter(true, this);
        rvFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvFeatured.setAdapter(featuredAdapter);

        recommendedAdapter = new RestaurantAdapter(false, this);
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommended.setAdapter(recommendedAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadHomeData();
    }
    private void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> setVisible(progress, isLoading != null && !isLoading));

        viewModel.getFeaturedRestaurants().observe(this, list -> {
            List<Restaurant> safe = list == null ? Collections.emptyList() : list;
            recommendedAdapter.submitList(safe);
            setVisible(emptyFeatured, safe.isEmpty());
        });

        viewModel.getRecommendedRestaurants().observe(this, list -> {
            List<Restaurant> safe = list == null ? Collections.emptyList() : list;
            featuredAdapter.submitList(safe);
            setVisible(emptyRecommended, safe.isEmpty());
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRestaurantClick(@NonNull Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("restaurant_id", restaurant.getId());
        startActivity(intent);
    }
}