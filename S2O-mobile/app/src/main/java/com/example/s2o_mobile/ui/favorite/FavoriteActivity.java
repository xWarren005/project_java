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
