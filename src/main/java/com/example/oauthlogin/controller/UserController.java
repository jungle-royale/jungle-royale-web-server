package com.example.oauthlogin.controller;


import com.example.oauthlogin.common.util.JwtTokenProvider;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        System.out.println("user = " + user.toString());
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 현재 사용자 추출
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> response = new HashMap<>();

        System.out.println("userId = " + userId);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-info")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new IllegalStateException("No authentication found");
        }

        String jwtToken = (String) authentication.getCredentials(); // JWT 토큰 추출
        if (!jwtTokenProvider.isValidToken(jwtToken)) {
            throw new IllegalStateException("Invalid JWT token");
        }

        String kakaoId = jwtTokenProvider.extractKakaoId(jwtToken);

        Map<String, Object> response = new HashMap<>();
        response.put("kakaoId", kakaoId);
        return ResponseEntity.ok(response);
    }
}
