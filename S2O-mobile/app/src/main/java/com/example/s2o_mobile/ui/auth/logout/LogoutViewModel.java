package com.example.s2o_mobile.ui.auth.logout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

public class LogoutViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    public MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LogoutViewModel(@NonNull Application application) {
        super(application);

        SessionManager sessionManager = new SessionManager(application);
        authRepository = AuthRepository.getInstance(sessionManager);
    }
}