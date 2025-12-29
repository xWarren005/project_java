package com.example.s2o_mobile.ui.search;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantAdapter;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.ui.restaurant.detail.RestaurantDetailActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantClickListener;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantListViewModel;

import java.util.Collections;
import java.util.List;


public class SearchActivity extends BaseActivity
        implements RestaurantClickListener {

    private EditText edtSearch;
    private RecyclerView rvResult;
    private View emptyView;

    private RestaurantAdapter adapter;
    private View progress;
    private RestaurantListViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        setupSearchListener();
        observeViewModel();

    }

    private void bindViews() {
        edtSearch = findViewById(R.id.edtSearch);
        rvResult = findViewById(R.id.rvResult);
        emptyView = findViewById(R.id.emptyView);
        progress = findViewById(R.id.progress);

    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, null);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(adapter);
    }
}
private void setupViewModel() {
    viewModel = new ViewModelProvider(this)
            .get(RestaurantListViewModel.class);
}

private void setupSearchListener() {
    edtSearch.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString().trim();
            if (keyword.isEmpty()) {
                adapter.submitList(Collections.emptyList());
                setVisible(emptyView, false);
            } else {
                viewModel.search(keyword);
            }
        }
    });
}

private void observeViewModel() {
    viewModel.getLoading().observe(this,
            isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

    viewModel.getRestaurants().observe(this, list -> {
        List<Restaurant> safe = list == null
                ? Collections.emptyList()
                : list;

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
    intent.putExtra(
            RestaurantDetailActivity.EXTRA_RESTAURANT_ID,
            restaurant.getId()
    );
    startActivity(intent);
}
