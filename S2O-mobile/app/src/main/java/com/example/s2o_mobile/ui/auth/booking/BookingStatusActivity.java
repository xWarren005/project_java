package com.example.s2o_mobile.ui.booking;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.s2o_mobile.R;

public class BookingStatusActivity extends AppCompatActivity {

    private TextView tvBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        tvBookingId = findViewById(R.id.tvBookingId);
        tvBookingId.setText("Booking Status");
    }
}
