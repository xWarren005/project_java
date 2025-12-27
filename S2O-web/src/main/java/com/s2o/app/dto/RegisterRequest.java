package com.s2o.app.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private String confirmPassword;
}
