package com.example.s2o_mobile.ui.auth.logout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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
public LiveData<Boolean> getLogoutResult() {
    return logoutResult;
}

public LiveData<String> getErrorMessage() {
    return errorMessage;
}

public void logout() {
    try {
        authRepository.logout();
        logoutResult.setValue(true);
    } catch (Exception e) {
        logoutResult.setValue(false);
        errorMessage.setValue("Đăng xuất thất bại");
    }
}
}