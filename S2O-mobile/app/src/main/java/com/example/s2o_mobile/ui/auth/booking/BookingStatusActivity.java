package com.example.s2o_mobile.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Booking;

public class BookingStatusActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    private BookingStatusViewModel viewModel;

    private ProgressBar progressBar;
    private TextView tvBookingId, tvRestaurant, tvTime, tvGuest, tvStatus;
    private Button btnRefresh;

    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, 0);

        progressBar = findViewById(R.id.progressBar);
        tvBookingId = findViewById(R.id.tvBookingId);
        tvRestaurant = findViewById(R.id.tvRestaurant);
        tvTime = findViewById(R.id.tvTime);
        tvGuest = findViewById(R.id.tvGuest);
        tvStatus = findViewById(R.id.tvStatus);
        btnRefresh = findViewById(R.id.btnRefresh);

        viewModel = new ViewModelProvider(this).get(BookingStatusViewModel.class);

        viewModel.getBooking().observe(this, booking -> {
            tvBookingId.setText("" + booking.getId());
            tvRestaurant.setText(booking.getStatus());
            tvTime.setText(booking.getNote());
            tvGuest.setText("" + booking.getRestaurantId());
            tvStatus.setText(booking.getBookingTime());
        });

        btnRefresh.setOnClickListener(v -> {
            viewModel.loadBookingStatus(0);
        });
    }
}
