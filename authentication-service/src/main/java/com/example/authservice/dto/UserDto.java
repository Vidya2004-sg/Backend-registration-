package com.example.authservice.dto;

public record UserDto(Long id, String username, String email, String phone, String password) {
}