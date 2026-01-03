package com.example.s2o_mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.ui.auth.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        tvName = new TextView(this);
        tvEmail = new TextView(this);
        tvPhone = new TextView(this);

        btnLogout = new Button(this);
        btnLogout.setText("Đăng xuất");

        root.addView(tvName);
        root.addView(tvEmail);
        root.addView(tvPhone);
        root.addView(btnLogout);

        setContentView(root);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUser().observe(this, this::renderUser);

        btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void renderUser(User u) {
        if (u == null) {
            tvName.setText("Họ tên: ?");
            tvEmail.setText("Email: ?");
            tvPhone.setText("SĐT: ?");
            return;
        }

        tvName.setText("Họ tên: " + u.getName());
        tvEmail.setText("Email: " + u.getEmail());
        tvPhone.setText("SĐT: " + u.getPhone());
    }
}
