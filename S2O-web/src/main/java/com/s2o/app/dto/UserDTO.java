package com.s2o.app.dto;

public class UserDTO {
    private Integer id;       // Khớp với Entity (Integer)
    private String username;
    private String password;  // Dữ liệu từ form nhập vào
    private String fullName;
    private String role;
    private String email;

    // 1. Constructor rỗng (BẮT BUỘC)
    public UserDTO() {
    }

    // 2. Constructor đầy đủ
    public UserDTO(Integer id, String username, String fullName, String email, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}