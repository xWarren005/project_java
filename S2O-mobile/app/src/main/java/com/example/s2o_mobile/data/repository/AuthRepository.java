package com.example.s2o_mobile.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.data.model.AuthResponse;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.AuthApi;
import com.example.s2o_mobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;
    private final SessionManager sessionManager;

    public AuthRepository(SessionManager sessionManager) {
        this.authApi = ApiService.getAuthApi();
        this.sessionManager = sessionManager;
    }
}
public void register(
        String username,
        String password,
        String fullName,
        String email,
        String phone,
        MutableLiveData<Boolean> registerResult,
        MutableLiveData<String> errorMessage
) {
    Call<User> call = authApi.register(
            username,
            password,
            fullName,
            email,
            phone
    );

    call.enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {

            if (response.isSuccessful()) {
                registerResult.setValue(true);
            } else {
                registerResult.setValue(false);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            registerResult.setValue(false);
        }
    });
}

public void login(
        String username,
        String password,
        MutableLiveData<Boolean> loginResult,
        MutableLiveData<String> errorMessage
) {
    Call<AuthResponse> call = authApi.login(username, password);

    call.enqueue(new Callback<AuthResponse>() {
        @Override
        public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
            if (response.isSuccessful() && response.body() != null) {

                AuthResponse authResponse = response.body();

                // Lưu session
                sessionManager.saveAuthToken(authResponse.getToken());
                sessionManager.saveUser(authResponse.getUser());

                loginResult.setValue(true);
            } else {
                errorMessage.setValue("Sai tài khoản hoặc mật khẩu");
                loginResult.setValue(false);
            }
        }

        @Override
        public void onFailure(Call<AuthResponse> call, Throwable t) {
            errorMessage.setValue(t.getMessage());
            loginResult.setValue(false);
        }
    });
}

public void logout() {
    sessionManager.clearSession();
}
public boolean isLoggedIn() {
    return sessionManager.getAuthToken() != null;
}
}


