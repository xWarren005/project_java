package com.example.s2o_mobile.ui.review;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Review;

import java.util.Collections;
import java.util.List;

public class ReviewActivity extends BaseActivity {

    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";

    private RecyclerView rvReviews;
    private View progress;
    private View emptyView;

    private ReviewAdapter adapter;
    private ReviewViewModel viewModel;

    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        restaurantId = getIntent() == null
                ? null
                : getIntent().getStringExtra(EXTRA_RESTAURANT_ID);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu mã nhà hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.loadReviews(restaurantId);
    }

    private void bindViews() {
        rvReviews = findViewById(R.id.rvReviews);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(null);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getReviews().observe(this, list -> {
            List<Review> safe = list == null ? Collections.emptyList() : list;
            adapter.submitList(safe);
            setVisible(emptyView, safe.isEmpty());
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisible(View v, boolean visible) {
        if (v == null) return;
        v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
