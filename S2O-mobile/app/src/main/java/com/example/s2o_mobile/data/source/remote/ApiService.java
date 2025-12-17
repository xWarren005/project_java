package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.utils.Constants;

import retrofit2.Retrofit;

public class ApiService {

    private Retrofit retrofit;

    public Retrofit getClient() {
        if (retrofit != null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
