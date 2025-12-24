package com.example.s2o_mobile.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;

public class BookingActivity extends AppCompatActivity {

    private BookingViewModel viewModel;

    private TextView tvRestaurantName;
    private EditText edtDateTime, edtGuestCount, edtNote;
    private Button btnBook;

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
        btnBook = findViewById(R.id.btnBook);

        tvRestaurantName.setText("Booking for #" + restaurantId);

        btnBook.setOnClickListener(v -> {
            String time = edtDateTime.getText().toString();
            int guest = Integer.parseInt(edtGuestCount.getText().toString());

            viewModel.createBooking(
                    guest,
                    time,
                    restaurantId,
                    edtNote.getText().toString()
            );

            Toast.makeText(this, "Booking...", Toast.LENGTH_SHORT).show();
        });
    }
}
