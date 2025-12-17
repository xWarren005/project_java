package com.example.s2o_mobile.ui.auth.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodbookingapp.data.model.User;
import com.example.foodbookingapp.data.repository.AuthRepository;
import com.example.foodbookingapp.utils.SessionManager;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> loginOk = new MutableLiveData<>(false);
    private final MutableLiveData<User> userLive = new MutableLiveData<>(null);

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository();
        this.sessionManager = new SessionManager(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getLoginOk() {
        return loginOk;
    }

    public LiveData<User> getUserLive() {
        return userLive;
    }
}
