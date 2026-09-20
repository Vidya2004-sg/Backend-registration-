package com.example.userservice.service;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepo;
import com.example.userservice.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User register(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .phone(request.phone())
                .password(PasswordUtil.hash(request.password()))
                .build();
        return userRepo.save(user);
    }

    public Optional<UserResponse> findByUsername(String username) {
        return userRepo.findByUsername(username).map(UserResponse::from);
    }
}