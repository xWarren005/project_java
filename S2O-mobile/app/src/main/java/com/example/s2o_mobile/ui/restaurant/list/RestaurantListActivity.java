package com.example.s2o_mobile.ui.restaurant.list;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView txtEmpty;

    private RestaurantAdapter adapter;
    private RestaurantListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        bindViews();
        setupRecycler();
        setupViewModel();
        observeState();

        viewModel.loadRestaurants();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerRestaurants);
        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
    }

    private void setupRecycler() {
        adapter = new RestaurantAdapter(new ArrayList<>(), restaurant -> {
            Toast.makeText(this, "Da chon: " + safe(restaurant.getName()), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {

        RestaurantRepository repo = new RestaurantRepository();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(RestaurantListViewModel.class)) {
                    return (T) new RestaurantListViewModel(repo);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(RestaurantListViewModel.class);
    }

    private void observeState() {
        viewModel.getLoading().observe(this, isLoading -> {
            boolean loading = isLoading != null && isLoading;
            progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getRestaurants().observe(this, list -> {
            List<Restaurant> data = (list == null) ? new ArrayList<>() : list;

            adapter.setData(data);

            boolean empty = data.isEmpty();
            txtEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
