package com.example.s2o_mobile.ui.review;

import com.example.s2o_mobile.data.model.Review;

public interface ReviewActionListener {
    void onEdit(Review review);
    void onDelete(Review review);
}