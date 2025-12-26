package com.example.s2o_mobile.ui.booking.history;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Booking;

import java.util.Collections;
import java.util.List;

public class BookingHistoryActivity extends BaseActivity {

    private RecyclerView rvBookingHistory;
    private View progress;
    private View emptyView;

    private BookingHistoryAdapter adapter;
    private BookingHistoryViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        viewModel.loadBookingHistory();
    }
    private void bindViews() {
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.progress);

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter();
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);

    }

        private void setupViewModel() {
            viewModel = new ViewModelProvider(this).get(BookingHistoryViewModel.class);
        }

        private void observeViewModel() {
            viewModel.getLoading().observe(this, isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

            viewModel.getBookings().observe(this, list -> {
                List<Booking> safe = list == null ? Collections.emptyList() : list;
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
    }
