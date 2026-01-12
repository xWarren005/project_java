package com.example.s2o_mobile.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.ui.restaurant.detail.RestaurantDetailActivity;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantAdapter;
import com.example.s2o_mobile.ui.restaurant.list.RestaurantClickListener;

import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements RestaurantClickListener {

    private static final long DEBOUNCE_MS = 400;

    private EditText edtSearch;
    private RecyclerView rvResult;
    private View progress;
    private View emptyView;

    private RestaurantAdapter adapter;
    private SearchViewModel viewModel;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        setupSearchBox();
    }

    private void bindViews() {
        edtSearch = findViewById(R.id.edtSearch);
        rvResult = findViewById(R.id.rvResult);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(false, this);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getResults().observe(this, list -> {
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

    private void setupSearchBox() {
        if (edtSearch == null) return;

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleSearch(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void scheduleSearch(@NonNull String raw) {
        String keyword = raw.trim();

        if (pending != null) {
            handler.removeCallbacks(pending);
        }

        pending = () -> {
            if (keyword.isEmpty()) {
                viewModel.clear();
                adapter.submitList(Collections.emptyList());
                setVisible(emptyView, false);
                return;
            }
            viewModel.search(keyword);
        };

        handler.postDelayed(pending, DEBOUNCE_MS);
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

    @Override
    protected void onDestroy() {
        if (pending != null) {
            handler.removeCallbacks(pending);
            pending = null;
        }
        super.onDestroy();
    }
}
