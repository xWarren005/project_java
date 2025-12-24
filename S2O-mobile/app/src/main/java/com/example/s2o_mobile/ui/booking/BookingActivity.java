package com.example.s2o_mobile.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private BookingViewModel viewModel;

    private TextView tvRestaurantName;
    private EditText edtDateTime, edtGuestCount, edtNote;
    private Button btnPickDateTime, btnBook;

    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        restaurantId = getIntent().getIntExtra("ID", 0);

        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        edtDateTime = findViewById(R.id.edtDateTime);
        edtGuestCount = findViewById(R.id.edtGuestCount);
        edtNote = findViewById(R.id.edtNote);
        btnPickDateTime = findViewById(R.id.btnPickDateTime);
        btnBook = findViewById(R.id.btnBook);

        tvRestaurantName.setText("Booking");

        btnPickDateTime.setOnClickListener(v -> pickDate());

        btnBook.setOnClickListener(v -> {
            String time = edtDateTime.getText().toString();
            int guest = Integer.parseInt(edtGuestCount.getText().toString());

            viewModel.createBooking(
                    restaurantId,
                    time,
                    guest,
                    edtNote.getText().toString()
            );
        });
    }

    private void pickDate() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    edtDateTime.setText(d + "/" + (m + 1) + "/" + y);
                    pickTime();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void pickTime() {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(
                this,
                (view, h, min) -> {
                    edtDateTime.setText(h + ":" + min);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        ).show();
    }
}
