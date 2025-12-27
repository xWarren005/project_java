package com.s2o.app.repository;

import com.s2o.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm user theo username khi login
    Optional<User> findByUsername(String username);
}
