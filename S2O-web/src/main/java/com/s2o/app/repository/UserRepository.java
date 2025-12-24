package com.s2o.app.repository;

import com.s2o.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm user theo username khi login
    Optional<User> findByUsername(String username);
}
