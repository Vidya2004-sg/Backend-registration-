package com.example.userservice.dto;

public record RegisterRequest(String username, String email, String phone, String password) {
}