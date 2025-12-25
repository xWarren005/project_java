package com.example.s2o_mobile.ui.booking;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Booking;

public class BookingStatusActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    private BookingStatusViewModel viewModel;
    private TextView tvBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        tvBookingId = findViewById(R.id.tvBookingId);

        viewModel = new ViewModelProvider(this).get(BookingStatusViewModel.class);

        viewModel.getBooking().observe(this, booking -> {
            tvBookingId.setText("Mã: " + booking.getId());
        });

        viewModel.loadBookingStatus(0);
    }
}
