package com.example.backend.userservice.controller;

import com.example.backend.userservice.model.User;
import com.example.backend.userservice.service.UserService;
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
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class UserControl {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request,
                                                     HttpServletResponse response) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (request.name() == null || request.password() == null
                || request.name().isBlank() || request.password().isBlank()) {
            body.put("success", false);
            body.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(body);
        }
        if (!userService.login(request.name(), request.password(), response)) {
            body.put("success", false);
            body.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(body);
        }
        User user = userService.findByName(request.name());
        body.put("success", true);
        body.put("message", "Login successful");
        body.put("name", user != null ? user.getName() : request.name());
        return ResponseEntity.ok(body);
    }

    public record LoginRequest(String name, String password) {
    }
}