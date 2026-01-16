package com.example.s2o_mobile.ui.restaurant.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Restaurant;

public class RestaurantDetailActivity extends BaseActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

    private View progress;

    private ImageView imgCover;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvRating;
    private TextView tvDescription;

    private ImageButton btnBack;

    private RestaurantDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        bindViews();
        setupViewModel();
        observeViewModel();

        int restaurantId = getIntent() == null
                ? -1
                : getIntent().getIntExtra(EXTRA_RESTAURANT_ID, -1);
        if (restaurantId <= 0) {
            Toast.makeText(this, "Thiếu mã nhà hàng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.loadRestaurantDetail(restaurantId);
    }

    private void bindViews() {
        progress = findViewById(R.id.progress);

        imgCover = findViewById(R.id.imgCover);
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvRating = findViewById(R.id.tvRating);
        tvDescription = findViewById(R.id.tvDescription);

        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getRestaurant().observe(this, restaurant -> {
            if (restaurant == null) return;
            bindRestaurant(restaurant);
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg == null || msg.trim().isEmpty()) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void bindRestaurant(@NonNull Restaurant restaurant) {
        if (tvName != null) tvName.setText(safe(restaurant.getName()));
        if (tvAddress != null) tvAddress.setText(safe(restaurant.getAddress()));
        if (tvDescription != null) tvDescription.setText(safe(restaurant.getDescription()));

        if (tvRating != null) {
            tvRating.setText(String.valueOf(restaurant.getAvgRating()));
        }

        if (imgCover != null) {
            imgCover.setVisibility(View.VISIBLE);
        }
    }

    private void setVisible(View view, boolean visible) {
        if (view == null) return;
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
