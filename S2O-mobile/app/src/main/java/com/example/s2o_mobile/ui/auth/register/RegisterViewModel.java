package com.example.s2o_mobile.ui.auth.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.s2o_mobile.data.repository.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {

    protected AuthRepository authRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }
}
