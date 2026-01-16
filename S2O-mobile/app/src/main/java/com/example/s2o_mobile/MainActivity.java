package com.example.s2o_mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.s2o_mobile.ui.auth.login.LoginActivity;
import com.example.s2o_mobile.ui.home.HomeActivity;
import com.example.s2o_mobile.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
