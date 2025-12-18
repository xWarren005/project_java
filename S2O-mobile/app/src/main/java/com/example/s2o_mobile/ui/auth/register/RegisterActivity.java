package com.example.s2o_mobile.ui.auth.register;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.s2o_mobile.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtUsername, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView txtGoLogin, txtError;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
