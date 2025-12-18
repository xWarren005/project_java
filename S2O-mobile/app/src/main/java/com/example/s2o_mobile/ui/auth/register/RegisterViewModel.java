package com.example.s2o_mobile.ui.auth.register;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.data.repository.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    // UI State
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>(null);

    // Field errors
    private final MutableLiveData<String> usernameError = new MutableLiveData<>(null);
    private final MutableLiveData<String> fullNameError = new MutableLiveData<>(null);
    private final MutableLiveData<String> emailError = new MutableLiveData<>(null);
    private final MutableLiveData<String> phoneError = new MutableLiveData<>(null);
    private final MutableLiveData<String> passwordError = new MutableLiveData<>(null);

    // Result
    private final MutableLiveData<Boolean> registerOk = new MutableLiveData<>(false);
    private final MutableLiveData<User> userLive = new MutableLiveData<>(null);

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository();
    }

    // ===== Getters =====
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMessage() { return message; }

    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getFullNameError() { return fullNameError; }
    public LiveData<String> getEmailError() { return emailError; }
    public LiveData<String> getPhoneError() { return phoneError; }
    public LiveData<String> getPasswordError() { return passwordError; }

    public LiveData<Boolean> getRegisterOk() { return registerOk; }
    public LiveData<User> getUserLive() { return userLive; }

    // ===== Action =====
    public void register(String username, String fullName, String email, String phone, String password) {
        clearState();

        String u = safeTrim(username);
        String fn = safeTrim(fullName);
        String em = safeTrim(email);
        String ph = safeTrim(phone);
        String pw = password == null ? "" : password;

        if (!validate(u, fn, em, ph, pw)) return;

        loading.setValue(true);

        authRepository.register(u, fn, em, ph, pw, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(User user) {
                loading.postValue(false);
                userLive.postValue(user);
                registerOk.postValue(true);
            }

            @Override
            public void onError(String err) {
                loading.postValue(false);

                // map lỗi đơn giản theo nội dung message server (nếu có)
                if (!TextUtils.isEmpty(err)) {
                    String lower = err.toLowerCase();
                    if (lower.contains("username")) usernameError.postValue(err);
                    else if (lower.contains("email")) emailError.postValue(err);
                    else if (lower.contains("mật khẩu") || lower.contains("password")) passwordError.postValue(err);
                    else message.postValue(err);
                } else {
                    message.postValue("Đăng ký thất bại. Vui lòng thử lại.");
                }
            }
        });
    }

    // ===== Validate =====
    private boolean validate(String username, String fullName, String email, String phone, String password) {
        boolean ok = true;

        // Username
        if (TextUtils.isEmpty(username)) {
            usernameError.setValue("Vui lòng nhập username");
            ok = false;
        } else if (username.length() < 4) {
            usernameError.setValue("Username tối thiểu 4 ký tự");
            ok = false;
        } else if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            usernameError.setValue("Username chỉ gồm chữ, số và . _ -");
            ok = false;
        }

        // Full name (không bắt buộc, nhưng nếu nhập quá ngắn thì nhắc)
        if (!TextUtils.isEmpty(fullName) && fullName.length() < 2) {
            fullNameError.setValue("Họ tên quá ngắn");
            ok = false;
        }

        // Email
        if (TextUtils.isEmpty(email)) {
            emailError.setValue("Vui lòng nhập email");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setValue("Email không hợp lệ");
            ok = false;
        }

        // Phone (không bắt buộc, nhưng nếu nhập thì check cơ bản)
        if (!TextUtils.isEmpty(phone)) {
            String digits = phone.replaceAll("\\s+", "");
            if (!digits.matches("^\\+?[0-9]{9,12}$")) {
                phoneError.setValue("Số điện thoại không hợp lệ");
                ok = false;
            }
        }

        // Password
        if (TextUtils.isEmpty(password)) {
            passwordError.setValue("Vui lòng nhập mật khẩu");
            ok = false;
        } else if (password.length() < 6) {
            passwordError.setValue("Mật khẩu tối thiểu 6 ký tự");
            ok = false;
        }

        return ok;
    }

    // ===== Helpers =====
    private void clearState() {
        loading.setValue(false);
        message.setValue(null);

        usernameError.setValue(null);
        fullNameError.setValue(null);
        emailError.setValue(null);
        phoneError.setValue(null);
        passwordError.setValue(null);

        registerOk.setValue(false);
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
