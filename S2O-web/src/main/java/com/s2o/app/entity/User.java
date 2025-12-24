package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data // Lombok tự sinh getter/setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    // Lưu password dạng hash (BCrypt)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false)
    private String role; // USER / ADMIN / MANAGER
    @Column(name = "is_active")
    private Boolean isActive = true;
}
