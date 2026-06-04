package com.example.demo.ss18_b1_javaservice.service;

import com.example.demo.ss18_b1_javaservice.dto.AuthResponse;
import com.example.demo.ss18_b1_javaservice.dto.LoginRequest;
import com.example.demo.ss18_b1_javaservice.dto.RegisterRequest;
import com.example.demo.ss18_b1_javaservice.dto.UserResponse;
import com.example.demo.ss18_b1_javaservice.entity.User;
import com.example.demo.ss18_b1_javaservice.repository.UserRepository;
import com.example.demo.ss18_b1_javaservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Set<String> VALID_ROLES = Set.of("ROLE_USER", "ROLE_STAFF", "ROLE_ADMIN");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(normalizeRole(request.getRole()));

        return UserResponse.from(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer"
        );
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_USER";
        }

        String normalizedRole = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be ROLE_USER, ROLE_STAFF or ROLE_ADMIN");
        }

        return normalizedRole;
    }
}
