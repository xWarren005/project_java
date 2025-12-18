package com.example.s2o_mobile.ui.auth.logout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.s2o_mobile.data.repository.AuthRepository;
import com.example.s2o_mobile.utils.SessionManager;
public class LogoutViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    public LogoutViewModel(@NonNull Application application) {
        super(application);

        SessionManager sessionManager = new SessionManager(application);
        authRepository = AuthRepository.getInstance(sessionManager);
    }
}