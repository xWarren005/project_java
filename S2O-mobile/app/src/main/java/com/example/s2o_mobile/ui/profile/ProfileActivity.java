package com.example.s2o_mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.ui.auth.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;

    private ProgressBar progressBar;
    private TextView tvName, tvEmail, tvPhone, tvRank;
    private Button btnEdit, btnMemberRank, btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        progressBar = new ProgressBar(this);
        tvName = new TextView(this);
        tvEmail = new TextView(this);
        tvPhone = new TextView(this);
        tvRank = new TextView(this);

        btnEdit = new Button(this);
        btnEdit.setText("Chỉnh sửa");

        btnMemberRank = new Button(this);
        btnMemberRank.setText("Hạng thành viên");

        btnLogout = new Button(this);
        btnLogout.setText("Đăng xuất");

        root.addView(progressBar);
        root.addView(tvName);
        root.addView(tvEmail);
        root.addView(tvPhone);
        root.addView(tvRank);
        root.addView(btnEdit);
        root.addView(btnMemberRank);
        root.addView(btnLogout);

        setContentView(root);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getLoggedIn().observe(this, logged -> {
            if (!Boolean.TRUE.equals(logged)) {
                Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUser().observe(this, this::renderUser);

        btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void renderUser(User u) {
        if (u == null) return;

        tvName.setText("Họ tên: " + u.getName());
        tvEmail.setText("Email: " + u.getEmail());
        tvPhone.setText("SĐT: " + u.getPhone());
        tvRank.setText("Hạng: " + u.getRank());
    }
}
