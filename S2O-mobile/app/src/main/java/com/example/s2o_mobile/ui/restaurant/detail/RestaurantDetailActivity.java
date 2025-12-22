package com.example.s2o_mobile.ui.restaurant.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;

public class RestaurantDetailActivity extends BaseActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

    private View progress;

    private ImageView imgCover;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvRating;
    private TextView tvDescription;
    private TextView tvPhone;

    private ImageButton btnBack;
    private View btnCall;
    private View btnMap;

    private RestaurantDetailViewModel viewModel;

    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        bindViews();
        setupViewModel();

        restaurantId = getIntent() != null ? getIntent().getStringExtra("restaurant_id") : null;
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu mã nhà hàng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void bindViews() {
        progress = findViewById(R.id.progress);

        imgCover = findViewById(R.id.imgCover);
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvRating = findViewById(R.id.tvRating);
        tvDescription = findViewById(R.id.tvDescription);
        tvPhone = findViewById(R.id.tvPhone);

        btnBack = findViewById(R.id.btnBack);
        btnCall = findViewById(R.id.btnCall);
        btnMap = findViewById(R.id.btnMap);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
    }
}

private void observeViewModel() {
    viewModel.getLoading().observe(this, isLoading -> {
        if (progress != null) {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.GONE : View.VISIBLE);
        }
    });

    viewModel.getRestaurant().observe(this, restaurant -> {
        if (restaurant == null) return;

        if (tvName != null) tvName.setText(safe(restaurant.getName()));
        if (tvAddress != null) tvAddress.setText(safe(restaurant.getPhone()));
        if (tvPhone != null) tvPhone.setText(safe(restaurant.getPhone()));
        if (tvDescription != null) tvDescription.setText(safe(restaurant.getDescription()));

        if (tvRating != null) {
            tvRating.setText(restaurant.getRating() == null ? "" : String.valueOf(restaurant.getRating()));
        }
    });

    viewModel.getErrorMessage().observe(this, msg -> {
        if (msg != null && !msg.trim().isEmpty()) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    });
}

private String safe(String s) {
    return s == null ? "" : s;
}
