package com.example.s2o_mobile.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.MemberRank;

public class MemberRankActivity extends BaseActivity {

    private TextView tvRankName;
    private TextView tvBenefit;
    private TextView tvPoint;

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_rank);

        bindViews();
        setupViewModel();
        bindData();
    }

    private void bindViews() {
        tvRankName = findViewById(R.id.tvRankName);
        tvBenefit = findViewById(R.id.tvBenefit);
        tvPoint = findViewById(R.id.tvPoint);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void bindData() {
        MemberRank rank = viewModel.getMemberRank();
        if (rank == null) return;

        tvRankName.setText(rank.getName());
        tvBenefit.setText(rank.getBenefit());
        tvPoint.setText(String.valueOf(rank.getPoint()));
    }
}
