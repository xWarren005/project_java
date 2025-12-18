package com.example.s2o_mobile.ui.auth.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.repository.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {

    protected AuthRepository authRepository;

    // UI state
    protected MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    protected MutableLiveData<String> message = new MutableLiveData<>();

    // Field errors
    protected MutableLiveData<String> usernameError = new MutableLiveData<>();
    protected MutableLiveData<String> fullNameError = new MutableLiveData<>();
    protected MutableLiveData<String> emailError = new MutableLiveData<>();
    protected MutableLiveData<String> phoneError = new MutableLiveData<>();
    protected MutableLiveData<String> passwordError = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    // Getters
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMessage() { return message; }

    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getFullNameError() { return fullNameError; }
    public LiveData<String> getEmailError() { return emailError; }
    public LiveData<String> getPhoneError() { return phoneError; }
    public LiveData<String> getPasswordError() { return passwordError; }
}
