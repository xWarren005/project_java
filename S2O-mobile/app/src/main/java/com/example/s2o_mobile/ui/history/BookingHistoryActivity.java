package com.example.s2o_mobile.ui.booking.history;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.progress);

        adapter = new BookingHistoryAdapter();
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        progress.setVisibility(View.GONE);

        List<Booking> list = Collections.emptyList();
        adapter.submitList(list);

        if (list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        Toast.makeText(this, "Tải lịch sử đặt bàn", Toast.LENGTH_SHORT).show();
    }
}
