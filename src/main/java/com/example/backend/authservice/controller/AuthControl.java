package com.example.backend.authservice.controller;

import com.example.backend.authservice.service.AuthService;
import com.example.backend.userservice.model.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class AuthControl {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegistrationRequest request,
                                                        HttpServletResponse response) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (request.name() == null || request.password() == null || request.email() == null
                || request.confirmPassword() == null || request.phno() == null
                || request.name().isBlank() || request.password().isBlank() || request.email().isBlank()
                || request.confirmPassword().isBlank() || request.phno().isBlank()) {
            body.put("success", false);
            body.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(body);
        }
        if (!request.password().equals(request.confirmPassword())) {
            body.put("success", false);
            body.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(body);
        }
        User user = User.builder()
                .name(request.name())
                .password(request.password())
                .email(request.email())
                .phno(request.phno())
                .build();
        boolean created = authService.register(user, response);
        if (!created) {
            body.put("success", false);
            body.put("message", "Username or email already exists");
            return ResponseEntity.badRequest().body(body);
        }
        body.put("success", true);
        body.put("message", "Registration successful");
        body.put("name", user.getName());
        return ResponseEntity.ok(body);
    }

    public record RegistrationRequest(String name, String password, String confirmPassword,
                                      String email, String phno) {
    }
}