package com.s2o.app.service;

import com.s2o.app.dto.UserDTO;
import com.s2o.app.entity.User;
import com.s2o.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    // [2] Khởi tạo bộ mã hóa mật khẩu giống hệt bên UserService
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. Lấy danh sách
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(u -> new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getRole()
        )).collect(Collectors.toList());
    }

    public long getTotalUsers() { return userRepository.count(); }

    // 2. Thêm mới
    public void createUser(UserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        // Xử lý mật khẩu (Gán vào passwordHash)
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPasswordHash(encodedPassword);

        userRepository.save(user);
    }

    // 3. Cập nhật
    public void updateUser(Integer id, UserDTO dto) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User u = optional.get();
            u.setFullName(dto.getFullName());
            u.setEmail(dto.getEmail());
            u.setRole(dto.getRole());

            // Chỉ đổi mật khẩu nếu người dùng nhập mới
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(dto.getPassword());
                u.setPasswordHash(encodedPassword);
            }
            userRepository.save(u);
        }
    }

    // 4. Xóa
    public void deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }
    // Thêm hàm Login dành cho hệ thống
    public User loginSystem(String username, String password, String selectedRole) {
        // 1. Tìm user theo username
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return null; // Không tìm thấy tài khoản
        }

        User user = userOpt.get();

        // 2. Kiểm tra mật khẩu (đã mã hóa)
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return null; // Sai mật khẩu
        }

        // 3. [QUAN TRỌNG] Kiểm tra VAI TRÒ (Role)
        // Người dùng chọn role nào ở form thì tài khoản trong DB phải có role đó
        // Ví dụ: DB là "CUSTOMER" mà chọn đăng nhập "ADMIN" -> Chặn ngay.
        if (!user.getRole().equals(selectedRole)) {
            return null; // Sai vai trò (VD: User thường cố đăng nhập trang Admin)
        }

        // 4. Kiểm tra tài khoản có bị khóa không (nếu có trường active/enabled)
        // if (!user.isEnabled()) return null;

        return user; // Đăng nhập thành công
    }
}