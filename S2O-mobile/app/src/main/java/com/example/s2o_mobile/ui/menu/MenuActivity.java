package com.example.s2o_mobile.ui.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.repository.MenuRepository;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView txtEmpty;

    private MenuAdapter adapter;
    private MenuViewModel viewModel;

    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantId = getIntent().getIntExtra("restaurantId", -1);

        bindViews();
        setupRecycler();
        setupViewModel();
        observeState();

        if (restaurantId != -1) {
            viewModel.loadMenu(restaurantId);
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setText("Khong tim thay nha hang");
        }
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerMenu);
        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
    }

    private void setupRecycler() {
        adapter = new MenuAdapter(new ArrayList<>(), item -> {
            // Xu ly click mon an neu can (mo man chi tiet / them vao gio)
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        MenuRepository repo = new MenuRepository();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(MenuViewModel.class)) {
                    return (T) new MenuViewModel(repo);
                }
                throw new IllegalArgumentException("Unknown ViewModel");
            }
        }).get(MenuViewModel.class);
    }

    private void observeState() {
        viewModel.getLoading().observe(this, loading -> {
            progress.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getMenuItems().observe(this, list -> {
            boolean empty = list == null || list.isEmpty();

            txtEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);

            if (!empty) {
                adapter.setData(list);
            }
        });

        viewModel.getError().observe(this, err -> {
            if (err != null && !err.trim().isEmpty()) {
                txtEmpty.setVisibility(View.VISIBLE);
                txtEmpty.setText(err);
            }
        });
    }
}
