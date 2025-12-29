package com.example.s2o_mobile.ui.search;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantAdapter;

public class SearchActivity extends BaseActivity {

    private EditText edtSearch;
    private RecyclerView rvResult;
    private View emptyView;

    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bindViews();
        setupRecyclerView();
    }

    private void bindViews() {
        edtSearch = findViewById(R.id.edtSearch);
        rvResult = findViewById(R.id.rvResult);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, null);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(adapter);
    }
}
