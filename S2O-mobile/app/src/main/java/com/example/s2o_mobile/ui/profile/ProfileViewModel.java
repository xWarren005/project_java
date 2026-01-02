package com.example.s2o_mobile.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.utils.SessionManager;

public class ProfileViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;
    private final MutableLiveData<User> user = new MutableLiveData<>(null);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        loadProfile();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void loadProfile() {
        User u = sessionManager.getUser();
        user.setValue(u);
    }
}
