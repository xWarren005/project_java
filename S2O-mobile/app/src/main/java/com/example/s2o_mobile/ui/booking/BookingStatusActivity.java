package com.example.s2o_mobile.ui.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Booking;

public class BookingStatusActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    private BookingStatusViewModel viewModel;

    private ProgressBar progressBar;
    private TextView tvBookingId;
    private TextView tvRestaurant;
    private TextView tvTime;
    private TextView tvGuest;
    private TextView tvStatus;
    private TextView tvNote;
    private Button btnRefresh;

    private int bookingId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
        if (bookingId <= 0) {
            Toast.makeText(this, "Thiếu bookingId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();

        viewModel = new ViewModelProvider(this).get(BookingStatusViewModel.class);
        observeVm();

        btnRefresh.setOnClickListener(v -> viewModel.loadBookingStatus(bookingId));

        viewModel.loadBookingStatus(bookingId);
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progressBar);
        tvBookingId = findViewById(R.id.tvBookingId);
        tvRestaurant = findViewById(R.id.tvRestaurant);
        tvTime = findViewById(R.id.tvTime);
        tvGuest = findViewById(R.id.tvGuest);
        tvStatus = findViewById(R.id.tvStatus);
        tvNote = findViewById(R.id.tvNote);
        btnRefresh = findViewById(R.id.btnRefresh);
    }

    private void observeVm() {
        viewModel.getLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            if (btnRefresh != null) btnRefresh.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getBooking().observe(this, booking -> {
            if (booking != null) bindBooking(booking);
        });
    }

    private void bindBooking(Booking b) {
        if (tvBookingId != null) tvBookingId.setText("Mã đặt bàn: " + b.getId());

        if (tvRestaurant != null) {
            String name = b.getRestaurantName();
            tvRestaurant.setText((name == null || name.trim().isEmpty())
                    ? "Nhà hàng #" + b.getRestaurantId()
                    : name);
        }

        if (tvTime != null) tvTime.setText("Thời gian: " + nullSafe(b.getBookingTime()));
        if (tvGuest != null) tvGuest.setText("Số người: " + b.getGuestCount());
        if (tvStatus != null) tvStatus.setText("Trạng thái: " + nullSafe(b.getStatus()));
        if (tvNote != null) tvNote.setText("Ghi chú: " + nullSafe(b.getNote()));
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
