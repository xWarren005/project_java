package com.example.s2o_mobile.ui.booking.status;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Booking;

public class BookingStatusActivity extends BaseActivity {

    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvTable;
    private View progress;

    private BookingStatusViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        bindViews();
        setupViewModel();
        observeViewModel();

        String bookingId = getIntent().getStringExtra("booking_id");
        if (bookingId != null) {
            viewModel.loadBookingStatus(bookingId);
        }
    }

    private void bindViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvTime = findViewById(R.id.tvTime);
        tvTable = findViewById(R.id.tvTable);
        progress = findViewById(R.id.progress);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(BookingStatusViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getBooking().observe(this, booking -> {
            if (booking == null) return;
            bindBooking(booking);
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                showToast(msg);
            }
        });
    }

    private void bindBooking(Booking booking) {
        tvStatus.setText(booking.getStatus());
        tvTime.setText(booking.getTime());
        tvTable.setText(booking.getTableName());
    }

    private void setVisible(View view, boolean visible) {
        if (view == null) return;
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
