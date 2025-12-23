package com.s2o.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (để các lệnh POST/PUT/DELETE từ Postman hoặc JS không bị chặn)
                .csrf(csrf -> csrf.disable())

                // 2. Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép tất cả các request truy cập mà KHÔNG cần đăng nhập
                        .anyRequest().permitAll()
                );

        // Lưu ý: Tôi đã bỏ đoạn .formLogin() để nó không chuyển hướng sang trang login nữa

        return http.build();
    }
}