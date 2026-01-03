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

        buildUi();

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        observeVm();

        viewModel.loadProfile();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(android.view.View.GONE);

        tvName = new TextView(this);
        tvEmail = new TextView(this);
        tvPhone = new TextView(this);
        tvRank = new TextView(this);

        btnEdit = new Button(this);
        btnEdit.setText("Chỉnh sửa hồ sơ");

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

        btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void observeVm() {
        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUser().observe(this, this::renderUser);
    }

    private void renderUser(User u) {
        if (u == null) {
            tvName.setText("Họ tên: (trống)");
            tvEmail.setText("Email: (trống)");
            tvPhone.setText("SĐT: (trống)");
            tvRank.setText("Hạng: (trống)");
            return;
        }

        tvName.setText("Họ tên: " + safe(getField(u, "getFullName", "getName")));
        tvEmail.setText("Email: " + safe(getField(u, "getEmail")));
        tvPhone.setText("SĐT: " + safe(getField(u, "getPhone", "getPhoneNumber")));
        tvRank.setText("Hạng: " + safe(getField(u, "getRank", "getMemberRank")));
    }

    private String getField(User u, String... methods) {
        try {
            for (String m : methods) {
                try {
                    Object v = u.getClass().getMethod(m).invoke(u);
                    if (v != null) return String.valueOf(v);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String safe(String s) {
        if (s == null) return "(trống)";
        s = s.trim();
        return s.isEmpty() ? "(trống)" : s;
    }
}
