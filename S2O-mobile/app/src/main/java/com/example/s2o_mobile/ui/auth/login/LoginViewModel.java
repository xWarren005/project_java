package com.example.s2o_mobile.ui.auth.login;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.data.repository.AuthRepository;
import com.example.s2o_mobile.utils.SessionManager;

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
        this.sessionManager = new SessionManager(application);
        this.authRepository = AuthRepository.getInstance(sessionManager);
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


    public void login(String login, String password) {
        clearState();

        if (!validate(login, password)) return;

        loading.setValue(true);

        authRepository.login(login.trim(), password,
                new AuthRepository.LoginCallback() {
                    @Override
                    public void onSuccess(User user, String token) {
                        loading.postValue(false);

                        if (!TextUtils.isEmpty(token)) {
                            sessionManager.saveAuthToken(token);
                        }
                        if (user != null) {
                            sessionManager.saveUser(user);
                        }

                        userLive.postValue(user);
                        loginOk.postValue(true);
                    }

                    @Override
                    public void onError(String err) {
                        loading.postValue(false);
                        message.postValue(err);
                    }
                });
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
