package com.example.foodbookingapp.ui.auth.login;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

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

    private final MutableLiveData<String> loginError = new MutableLiveData<>(null);
    private final MutableLiveData<String> passwordError = new MutableLiveData<>(null);

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

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<Boolean> getLoginOk() {
        return loginOk;
    }

    public LiveData<User> getUserLive() {
        return userLive;
    }


    private boolean validate(String login, String password) {
        boolean ok = true;

        if (TextUtils.isEmpty(login)) {
            loginError.setValue("Vui lòng nhập email hoặc username");
            ok = false;
        } else if (login.contains("@") &&
                !Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            loginError.setValue("Email không hợp lệ");
            ok = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError.setValue("Vui lòng nhập mật khẩu");
            ok = false;
        } else if (password.length() < 6) {
            passwordError.setValue("Mật khẩu tối thiểu 6 ký tự");
            ok = false;
        }

        return ok;
    }

    private void clearState() {
        loading.setValue(false);
        message.setValue(null);
        loginError.setValue(null);
        passwordError.setValue(null);
        loginOk.setValue(false);
    }
}
