package com.example.s2o_mobile.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.utils.SessionManager;

public class ProfileViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;

    private final MutableLiveData<User> user = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> loggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        loadProfile();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> getLoggedIn() {
        return loggedIn;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadProfile() {
        try {
            String token = sessionManager.getAuthToken();
            boolean isLogged = token != null && !token.trim().isEmpty();
            loggedIn.setValue(isLogged);

            if (isLogged) {
                user.setValue(sessionManager.getUser());
            } else {
                user.setValue(null);
            }
        } catch (Exception e) {
            errorMessage.setValue(e.getMessage());
            loggedIn.setValue(false);
            user.setValue(null);
        }
    }
}
