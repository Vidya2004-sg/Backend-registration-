package com.example.userservice.controller;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (request.username() == null || request.email() == null
                || request.phone() == null || request.password() == null
                || request.username().isBlank() || request.email().isBlank()
                || request.phone().isBlank() || request.password().isBlank()) {
            body.put("success", false);
            body.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(body);
        }
        if (userService.existsByUsername(request.username())) {
            body.put("success", false);
            body.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }
        if (userService.existsByEmail(request.email())) {
            body.put("success", false);
            body.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }
        userService.register(request);
        body.put("success", true);
        body.put("message", "Registration successful");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}