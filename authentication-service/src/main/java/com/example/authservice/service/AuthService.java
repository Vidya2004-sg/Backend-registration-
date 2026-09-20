package com.example.authservice.service;

import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.UserDto;
import com.example.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public Optional<LoginResponse> login(String username, String rawPassword) {
        UserDto user = findUser(username);
        if (user == null) {
            return Optional.empty();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(rawPassword, user.password())) {
            return Optional.empty();
        }
        String token = jwtUtil.generateToken(user.username());
        return Optional.of(new LoginResponse(token, user.username()));
    }

    private UserDto findUser(String username) {
        try {
            return restTemplate.getForObject(
                    userServiceUrl + "/api/users/" + username,
                    UserDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}