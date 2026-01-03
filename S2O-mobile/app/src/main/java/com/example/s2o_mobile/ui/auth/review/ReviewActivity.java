package com.example.s2o_mobile.ui.review;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.Review;

import java.util.Collections;
import java.util.List;

public class ReviewActivity extends BaseActivity
        implements ReviewActionListener {


    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";

    private RecyclerView rvReviews;
    private View progress;
    private View emptyView;

    private ReviewAdapter adapter;
    private ReviewViewModel viewModel;

    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        btnWrite.setOnClickListener(v -> openWriteDialog(null));


        restaurantId = getIntent() == null
                ? null
                : getIntent().getStringExtra(EXTRA_RESTAURANT_ID);

        bindViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu mã nhà hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel.loadReviews(restaurantId);
    }

    private void bindViews() {
        rvReviews = findViewById(R.id.rvReviews);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
        btnWrite = findViewById(R.id.btnWriteReview);

    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(null);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this,
                isLoading -> setVisible(progress, Boolean.TRUE.equals(isLoading)));

        viewModel.getReviews().observe(this, list -> {
            List<Review> safe = list == null ? Collections.emptyList() : list;
            adapter.submitList(safe);
            setVisible(emptyView, safe.isEmpty());
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisible(View v, boolean visible) {
        if (v == null) return;
        v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
private void openWriteDialog(Review editing) {
    View view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_review_editor, null, false);

    TextView tvTitle = view.findViewById(R.id.tvTitle);
    RatingBar ratingBar = view.findViewById(R.id.ratingBar);
    EditText edtComment = view.findViewById(R.id.edtComment);

    boolean isEdit = editing != null;
    tvTitle.setText(isEdit ? "Chỉnh sửa đánh giá" : "Viết đánh giá");

    AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton("Hủy", (d, w) -> d.dismiss())
            .setPositiveButton(isEdit ? "Lưu" : "Gửi", null)
            .create();

    dialog.setOnShowListener(d -> {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = edtComment.getText().toString().trim();

            if (rating <= 0 || TextUtils.isEmpty(comment)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            Review r = new Review();
            r.setRestaurantId(restaurantId);
            r.setRating((double) rating);
            r.setComment(comment);

            if (isEdit) {
                viewModel.updateReview(editing.getId(), r);
            } else {
                viewModel.addReview(restaurantId, r);
            }

            dialog.dismiss();
        });
    });

    dialog.show();
}
private void confirmDelete(Review review) {
    new AlertDialog.Builder(this)
            .setTitle("Xóa đánh giá")
            .setMessage("Bạn chắc chắn muốn xóa đánh giá này?")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa",
                    (d, w) -> viewModel.deleteReview(review.getId()))
            .show();
}
@Override
public void onEdit(Review review) {
    openWriteDialog(review);
}

@Override
public void onDelete(Review review) {
    confirmDelete(review);
}
