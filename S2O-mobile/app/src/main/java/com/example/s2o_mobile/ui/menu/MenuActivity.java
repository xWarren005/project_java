package com.example.s2o_mobile.ui.menu;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.s2o_mobile.R;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected ProgressBar progress;
    protected TextView txtEmpty;

    protected int restaurantId;

    protected MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantId = getIntent().getIntExtra("restaurantId", -1);

        bindViews();
    }

    protected void bindViews() {
        recyclerView = findViewById(R.id.recyclerMenu);
        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantId = getIntent().getIntExtra("restaurantId", -1);

        bindViews();
        setupRecycler();
    }

    protected void setupRecycler() {
        adapter = new MenuAdapter(new ArrayList<>(), item -> {

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}
