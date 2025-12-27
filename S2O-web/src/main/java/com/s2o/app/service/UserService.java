package com.s2o.app.service;

import com.s2o.app.dto.RegisterRequest;
import com.s2o.app.entity.User;
import com.s2o.app.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Dùng BCrypt để so sánh mật khẩu
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hàm xử lý đăng nhập
     * @param username username nhập từ form
     * @param password password nhập từ form
     * @return User nếu đúng, null nếu sai
     */
    public User login(String username, String password) {

        // 1. Tìm user theo username
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return null; // Không tồn tại user
        }

        User user = userOpt.get();


        // 2. So sánh password nhập vào với password_hash trong DB
        boolean match = passwordEncoder.matches(password, user.getPasswordHash());
        if (!match) {
            return null; // Sai mật khẩu
        }

        // 3. Đúng username + password
        return user;
    }
    public String registerUser(RegisterRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Tài khoản này đã tồn tại!";
        }

        // 2. Kiểm tra mật khẩu nhập lại có khớp không
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Mật khẩu nhập lại không khớp!";
        }

        // 3. Tạo User mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setFullName(request.getFullName());

        // Mã hóa mật khẩu trước khi lưu
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Set quyền mặc định là Khách hàng
        newUser.setRole("CUSTOMER");

        // 4. Lưu vào DB
        userRepository.save(newUser);

        return "SUCCESS"; // Trả về thành công
    }
}

