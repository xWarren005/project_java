package com.example.s2o_mobile.ui.restaurant.detail;

import android.content.Intent;
import android.net.Uri;
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
    private TextView tvPhone;

    private ImageButton btnBack;
    private View btnCall;
    private View btnMap;

    private RestaurantDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        bindViews();
        setupViewModel();
        observeViewModel();

        String restaurantId = getIntent() != null ? getIntent().getStringExtra(EXTRA_RESTAURANT_ID) : null;
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
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
        tvPhone = findViewById(R.id.tvPhone);

        btnBack = findViewById(R.id.btnBack);
        btnCall = findViewById(R.id.btnCall);
        btnMap = findViewById(R.id.btnMap);
    }

   if (btnBack != null) {
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
    }
}

private void observeViewModel() {
    viewModel.getLoading().observe(this, isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

    viewModel.getRestaurant().observe(this, restaurant -> {
        if (restaurant == null) return;
        bindRestaurant(restaurant);
    });

    viewModel.getErrorMessage().observe(this, msg -> {
        if (msg == null || msg.trim().isEmpty()) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    });
}

private void bindRestaurant(@NonNull Restaurant r) {
        if (tvName != null) tvName.setText(safe(restaurant.getName()));
        if (tvAddress != null) tvAddress.setText(safe(restaurant.getPhone()));
        if (tvPhone != null) tvPhone.setText(safe(restaurant.getPhone()));
        if (tvDescription != null) tvDescription.setText(safe(restaurant.getDescription()));

        if (tvRating != null) {
            String ratingText = r.getRating() == null ? "" : String.valueOf(r.getRating());
            tvRating.setText(ratingText);
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
