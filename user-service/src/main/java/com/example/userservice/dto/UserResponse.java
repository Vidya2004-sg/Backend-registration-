package com.example.userservice.dto;

import com.example.userservice.model.User;

public record UserResponse(Long id, String username, String email, String phone, String password) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getPassword());
    }
}