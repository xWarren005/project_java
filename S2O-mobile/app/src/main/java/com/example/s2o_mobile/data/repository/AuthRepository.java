package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.AuthResponse;
import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.AuthApi;
import com.example.s2o_mobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private static AuthRepository instance;
    private final AuthApi authApi;
    private final SessionManager sessionManager;

    private AuthRepository(SessionManager sessionManager) {
        this.authApi = ApiService.getInstance().getAuthApi();
        this.sessionManager = sessionManager;
    }

    public static AuthRepository getInstance(SessionManager sessionManager) {
        if (instance == null) {
            instance = new AuthRepository(sessionManager);
        }
        return instance;
    }

    public interface LoginCallback {
        void onSuccess(User user, String token);

        void onError(String err);
    }

    public interface RegisterCallback {
        void onSuccess(User user);

        void onError(String err);
    }

    public void register(String username,
                         String fullName,
                         String email,
                         String phone,
                         String password,
                         RegisterCallback callback) {
        Call<User> call = authApi.register(username, password, fullName, email, phone);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) callback.onSuccess(response.body());
                } else {
                    if (callback != null) callback.onError("Đăng ký thất bại");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage() == null ? "Lỗi kết nối" : t.getMessage());
                }
            }
        });
    }

    public void login(String username,
                      String password,
                      LoginCallback callback) {
        Call<AuthResponse> call = authApi.login(username, password);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    sessionManager.saveAuthToken(authResponse.getToken());
                    sessionManager.saveUser(authResponse.getUser());

                    if (callback != null) {
                        callback.onSuccess(authResponse.getUser(), authResponse.getToken());
                    }
                } else {
                    if (callback != null) callback.onError("Sai tài khoản hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t.getMessage() == null ? "Lỗi kết nối" : t.getMessage());
                }
            }
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}


