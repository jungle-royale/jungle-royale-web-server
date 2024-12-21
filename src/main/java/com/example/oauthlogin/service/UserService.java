package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.RefreshToken;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.domain.UserDto;
import com.example.oauthlogin.repository.RefreshTokenRepository;
import com.example.oauthlogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).isPresent();
    }

    public UserDto saveOrUpdateUser(String kakaoId, String username, String refreshToken, Integer refreshTokenExpiry) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElse(new User());

        user.setKakaoId(kakaoId);
        user.setUsername(username);
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        User savedUser = userRepository.save(user);

        saveRefreshToken(savedUser, refreshToken, refreshTokenExpiry);

        return UserDto.builder()
                .id(savedUser.getId())
                .kakaoId(savedUser.getKakaoId())
                .username(savedUser.getUsername())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .lastLoginAt(savedUser.getLastLoginAt())
                .build();
    }

    private void saveRefreshToken(User user, String token, Integer expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);

        refreshTokenRepository.save(refreshToken);
    }
}
