package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.RefreshToken;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.domain.UserDto;
import com.example.oauthlogin.repository.RefreshTokenRepository;
import com.example.oauthlogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public void updateRefreshTokenByKakaoId(String kakaoId, String newRefreshToken, Integer newExpiry) {
        // 유저 확인
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Kakao ID: " + kakaoId));

        // 리프레시 토큰 존재 여부 확인
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(user.getId());

        if (existingTokenOpt.isPresent()) {
            // 토큰이 존재하면 수정
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.setToken(newRefreshToken);
            existingToken.setExpiresAt(newExpiry);
            refreshTokenRepository.save(existingToken);
        } else {
            // 토큰이 없으면 새로 추가
            RefreshToken newToken = new RefreshToken();
            newToken.setUser(user);
            newToken.setToken(newRefreshToken);
            newToken.setExpiresAt(newExpiry);
            refreshTokenRepository.save(newToken);
        }
    }

    public UserDto getUserByKakaoId(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Kakao ID: " + kakaoId));

        return UserDto.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

}
