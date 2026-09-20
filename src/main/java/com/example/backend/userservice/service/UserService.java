package com.example.backend.userservice.service;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final AuthRepo authRepo;
    private final JwtUtil jwtUtil;

    @Transactional
    public boolean login(String name, String password, HttpServletResponse response) {
        Optional<User> userOpt = userRepo.findByName(name);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!PasswordUtil.matches(password, user.getPassword())) {
            return false;
        }
        authRepo.deleteByUid(user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getName());
        LocalDateTime now = LocalDateTime.now();
        authRepo.save(JwtToken.builder()
                .uid(user.getId())
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

    public User findByName(String name) {
        return userRepo.findByName(name).orElse(null);
    }
}