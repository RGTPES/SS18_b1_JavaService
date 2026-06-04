package com.example.demo.ss18_b1_javaservice.dto;

import com.example.demo.ss18_b1_javaservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String phone;
    private String email;
    private String role;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getPhone(),
                user.getEmail(),
                user.getRole()
        );
    }
}
