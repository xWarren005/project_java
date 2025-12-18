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
    private final MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
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
        if (!authRepository.isLoggedIn();
        logoutResult.setValue(true);
        return;
    }

    authRepository.logout();
    logoutResult.setValue(true);

} catch (Exception e) {
        logoutResult.setValue(false);
            errorMessage.setValue(
        e.getMessage() != null ? e.getMessage() : "Đăng xuất thất bại"
        );
        }
        }
        }
public boolean isLoggedIn() {
    try {
        return authRepository.isLoggedIn();
    } catch (Exception e) {
        return false;
    }
}
}