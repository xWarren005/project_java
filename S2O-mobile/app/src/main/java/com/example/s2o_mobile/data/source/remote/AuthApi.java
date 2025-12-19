package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.AuthResponse;
import com.example.s2o_mobile.data.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {


    @FormUrlEncoded
    @POST("api/mobile/auth/login")
    Call<AuthResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/mobile/auth/register")
    Call<User> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("phone") String phone
    );
}
