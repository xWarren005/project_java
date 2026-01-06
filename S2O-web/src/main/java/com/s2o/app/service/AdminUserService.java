package com.s2o.app.service;

import com.s2o.app.dto.UserDTO;
import com.s2o.app.entity.User;
import com.s2o.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(u -> new UserDTO(
                u.getId().longValue(), // User ID là Integer, DTO là Long
                u.getFullName() != null ? u.getFullName() : u.getUsername(),
                u.getEmail(),
                u.getRole(),
                "active"
        )).collect(Collectors.toList());
    }

    public long getTotalUsers() { return userRepository.count(); }
    public long getActiveUsers() { return userRepository.count(); } // Tạm lấy tổng
    public int getNewUsersThisWeek() { return 0; }
}