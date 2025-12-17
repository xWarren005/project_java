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

