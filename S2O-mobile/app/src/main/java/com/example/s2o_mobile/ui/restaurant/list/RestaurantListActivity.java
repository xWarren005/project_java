package com.example.s2o_mobile.ui.restaurant.list;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Restaurant;


import java.util.ArrayList;
public class RestaurantListActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected ProgressBar progress;
    protected TextView txtEmpty;
    protected RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        bindViews();
        setupRecycler();
    }

    protected void bindViews() {
        recyclerView = findViewById(R.id.recyclerRestaurants);
        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
    }
    protected void setupRecycler() {
        adapter = new RestaurantAdapter(new ArrayList<>(), restaurant -> {
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
