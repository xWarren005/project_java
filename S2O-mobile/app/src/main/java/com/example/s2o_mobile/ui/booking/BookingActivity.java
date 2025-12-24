package com.example.s2o_mobile.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;

import java.util.Calendar;
import java.util.Locale;


public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";

    private BookingViewModel viewModel;

    private ProgressBar progressBar;
    private TextView tvRestaurantName;
    private EditText edtDateTime, edtGuestCount, edtNote;
    private Button btnPickDateTime, btnBook;

    private int restaurantId = -1;
    private String restaurantName = "";

    // Lưu giá trị date-time đã chọn
    private final Calendar selectedCal = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        restaurantId = getIntent().getIntExtra(EXTRA_RESTAURANT_ID, -1);
        restaurantName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        if (restaurantName == null) restaurantName = "";

        bindViews();
        setupUi();
        observeVm();
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progressBar);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);

        edtDateTime = findViewById(R.id.edtDateTime);
        edtGuestCount = findViewById(R.id.edtGuestCount);
        edtNote = findViewById(R.id.edtNote);

        btnPickDateTime = findViewById(R.id.btnPickDateTime);
        btnBook = findViewById(R.id.btnBook);
    }

    private void setupUi() {
        if (restaurantId <= 0) {
            Toast.makeText(this, "Thiếu restaurantId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (tvRestaurantName != null) {
            tvRestaurantName.setText(restaurantName.isEmpty() ? "Đặt bàn" : restaurantName);
        }

        if (edtDateTime != null) {
            edtDateTime.setKeyListener(null);
            edtDateTime.setFocusable(false);
        }

        btnPickDateTime.setOnClickListener(v -> pickDateThenTime());

        btnBook.setOnClickListener(v -> {
            String bookingTimeIso = buildIsoDateTime();
            String guestStr = edtGuestCount.getText() == null ? "" : edtGuestCount.getText().toString().trim();
            String note = edtNote.getText() == null ? "" : edtNote.getText().toString().trim();

            if (bookingTimeIso == null) {
                Toast.makeText(this, "Vui lòng chọn ngày giờ", Toast.LENGTH_SHORT).show();
                return;
            }

            int guestCount;
            try {
                guestCount = Integer.parseInt(guestStr);
            } catch (Exception e) {
                Toast.makeText(this, "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (guestCount <= 0) {
                Toast.makeText(this, "Số người phải > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi ViewModel tạo booking
            viewModel.createBooking(restaurantId, bookingTimeIso, guestCount, note);
        });
    }

    private void observeVm() {
        viewModel.getLoading().observe(this, loading -> {
            if (progressBar != null) {
                progressBar.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
            }
            btnBook.setEnabled(!Boolean.TRUE.equals(loading));
            btnPickDateTime.setEnabled(!Boolean.TRUE.equals(loading));
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCreateBookingOk().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(this, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // ===== DateTime Picker =====
    private void pickDateThenTime() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCal.set(Calendar.YEAR, year);
                    selectedCal.set(Calendar.MONTH, month);
                    selectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    pickTime();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void pickTime() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog tp = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCal.set(Calendar.MINUTE, minute);
                    selectedCal.set(Calendar.SECOND, 0);

                    // Hiển thị lên editText theo dạng dễ đọc
                    String display = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d %02d:%02d",
                            selectedCal.get(Calendar.DAY_OF_MONTH),
                            selectedCal.get(Calendar.MONTH) + 1,
                            selectedCal.get(Calendar.YEAR),
                            selectedCal.get(Calendar.HOUR_OF_DAY),
                            selectedCal.get(Calendar.MINUTE)
                    );
                    edtDateTime.setText(display);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tp.show();
    }

    private String buildIsoDateTime() {
        // Nếu user chưa bấm chọn, edtDateTime sẽ rỗng
        if (edtDateTime.getText() == null || edtDateTime.getText().toString().trim().isEmpty()) {
            return null;
        }

        int y = selectedCal.get(Calendar.YEAR);
        int m = selectedCal.get(Calendar.MONTH) + 1;
        int d = selectedCal.get(Calendar.DAY_OF_MONTH);
        int hh = selectedCal.get(Calendar.HOUR_OF_DAY);
        int mm = selectedCal.get(Calendar.MINUTE);

        return String.format(Locale.US, "%04d-%02d-%02dT%02d:%02d:00", y, m, d, hh, mm);
    }
}
