package com.s2o.app.dto;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;   // "Admin", "Restaurant Manager", "Customer"
    private String status; // "active", "inactive"

    public UserDTO(Long id, String name, String email, String role, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}