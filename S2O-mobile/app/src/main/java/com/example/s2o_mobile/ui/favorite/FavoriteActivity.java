package com.example.s2o_mobile.ui.favorite;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantAdapter;

public class FavoriteActivity extends BaseActivity {

    private RecyclerView rvFavorites;
    private View emptyView;

    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        bindViews();
        setupRecyclerView();
    }

    private void bindViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, null);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }
}
public class FavoriteActivity extends BaseActivity implements RestaurantClickListener {

    private RecyclerView rvFavorites;
    private View progress;
    private View emptyView;

    private RestaurantAdapter adapter;
    private FavoriteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        viewModel.loadFavorites();
    }

    private void bindViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getFavorites().observe(this, list -> {
            List<Restaurant> safe = list == null ? Collections.emptyList() : list;
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
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
        startActivity(intent);
    }
}