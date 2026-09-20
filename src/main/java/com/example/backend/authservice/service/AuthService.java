package com.example.backend.authservice.service;

import com.example.backend.authservice.model.JwtToken;
import com.example.backend.authservice.repo.AuthRepo;
import com.example.backend.userservice.model.User;
import com.example.backend.userservice.repo.UserRepo;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.PasswordUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final AuthRepo authRepo;
    private final JwtUtil jwtUtil;

    @Transactional
    public boolean register(User user, HttpServletResponse response) {
        if (userRepo.existsByName(user.getName()) || userRepo.existsByEmail(user.getEmail())) {
            return false;
        }
        user.setPassword(PasswordUtil.hash(user.getPassword()));
        User saved = userRepo.save(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getName());
        LocalDateTime now = LocalDateTime.now();
        authRepo.save(JwtToken.builder()
                .uid(saved.getId())
                .token(token)
                .create_at(now)
                .end_at(now.plusSeconds(jwtUtil.getExpirationMs() / 1000))
                .build());

        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
        return true;
    }
}