package com.example.s2o_mobile.data.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}