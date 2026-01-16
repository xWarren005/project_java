package com.example.s2o_mobile.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private ProgressBar progress;
    private TextView txtError;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        setupViewModel();
        observeState();
        setupActions();
    }

    private void bindViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progress = findViewById(R.id.progress);
        txtError = findViewById(R.id.txtError);
    }

    private void setupActions() {
        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = edtUsername.getText() != null ? edtUsername.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString() : "";
            viewModel.login(usernameOrEmail, password);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void observeState() {
        viewModel.getLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            progress.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!loading);
        });

        viewModel.getMessage().observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                txtError.setText(msg);
                txtError.setVisibility(View.VISIBLE);
            } else {
                txtError.setText("");
                txtError.setVisibility(View.GONE);
            }
        });

        viewModel.getLoginOk().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                goHome();
            }
        });
    }

    private void goHome() {

        try {
            Intent intent = new Intent(this, Class.forName("com.example.s2o_mobile.ui.home.HomeActivity"));
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            txtError.setText("Chua co HomeActivity. Tao HomeActivity hoac sua duong dan class.");
            txtError.setVisibility(View.VISIBLE);
        }
    }
}
