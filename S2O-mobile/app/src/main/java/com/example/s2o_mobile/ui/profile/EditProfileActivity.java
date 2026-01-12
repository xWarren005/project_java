package com.example.s2o_mobile.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.base.BaseActivity;
import com.example.s2o_mobile.data.model.User;
import com.example.s2o_mobile.utils.Validator;

public class EditProfileActivity extends BaseActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private Button btnSave;

    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bindViews();
        setupViewModel();
        bindData();
        setupActions();
    }

    private void bindViews() {
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void bindData() {
        User user = viewModel.getCurrentUser();
        if (user == null) return;

        edtName.setText(user.getName());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone());
    }

    private void setupActions() {
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = edtName.getText() == null ? "" : edtName.getText().toString().trim();
        String email = edtEmail.getText() == null ? "" : edtEmail.getText().toString().trim();
        String phone = edtPhone.getText() == null ? "" : edtPhone.getText().toString().trim();

        if (!Validator.isValidName(name)) {
            showToast("Tên không hợp lệ");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            showToast("Email không hợp lệ");
            return;
        }

        if (!Validator.isValidPhone(phone)) {
            showToast("Số điện thoại không hợp lệ");
            return;
        }

        User updated = new User();
        updated.setName(name);
        updated.setEmail(email);
        updated.setPhone(phone);

        viewModel.updateProfile(updated);
        showToast("Cập nhật thông tin thành công");
        finishSafe();
    }
}
