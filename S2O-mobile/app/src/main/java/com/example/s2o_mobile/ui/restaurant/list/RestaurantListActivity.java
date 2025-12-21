package com.example.s2o_mobile.ui.restaurant.list;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;

public class RestaurantListActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected ProgressBar progress;
    protected TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        bindViews();
    }

    protected void bindViews() {
        recyclerView = findViewById(R.id.recyclerRestaurants);
        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
    }
}
