package com.example.s2o_mobile.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.repository.AuthRepository;
import com.example.s2o_mobile.data.repository.AuthRepositoryImpl;
import com.example.s2o_mobile.utils.Validator;

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
        AuthRepository repo = new AuthRepositoryImpl();
        Validator validator = new Validator();
        LoginUseCase useCase = new LoginUseCase(repo, validator);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                    return (T) new LoginViewModel(useCase);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(LoginViewModel.class);
    }

    private void observeState() {
        viewModel.getState().observe(this, state -> {
            if (state == null) return;

            progress.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!state.loading);

            if (state.error != null && !state.error.trim().isEmpty()) {
                txtError.setText(state.error);
                txtError.setVisibility(View.VISIBLE);
            } else {
                txtError.setText("");
                txtError.setVisibility(View.GONE);
            }

            if (state.success) {
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
