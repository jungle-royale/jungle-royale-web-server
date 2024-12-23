package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.*;
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

    public User saveKakaoUser(String kakaoId, String username) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElse(new User());

        user.setKakaoId(kakaoId);
        user.setUsername(username);
        user.setRole(UserRole.MEMBER);
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    private void saveRefreshToken(User user, String token, Integer expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);

        refreshTokenRepository.save(refreshToken);
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

    public void kakaoUserJoin(String kakaoId, OAuthKakaoToken oAuthKakaoToken) {
        // 유저가 존재하지않으면 회원, 리프레시 토큰 저장
        if (!isUserExistsByKakaoId(kakaoId)) {
            User savedUser = saveKakaoUser(kakaoId, "테스터1");
            saveRefreshToken(savedUser, oAuthKakaoToken.getRefresh_token(), oAuthKakaoToken.getRefresh_token_expires_in());
            // 유저 등록 및 refreshtoken 등록
        } else {
            // 유저가 존재해 그럼 리프레시 토큰만 저장
            // refreshtoken 등록
            User user = userRepository.findByKakaoId(kakaoId)
                    .orElse(new User());
            saveRefreshToken(user, oAuthKakaoToken.getRefresh_token(), oAuthKakaoToken.getRefresh_token_expires_in());
        }
    }

    /**
     * 비회원 유저 생성
     * @return
     */
    public User registerGuest() {
        User guestUser = new User();
        return userRepository.save(guestUser.createGueutUser());
    }

    /**
     * 회원인지 비회원인지 판단
     * @param kakaoId
     * @return
     */
    public User findOrRegisterGuest(String kakaoId) {
        if (kakaoId == null || kakaoId.isEmpty()) {
            // 비회원 처리
            return registerGuest();
        }

        // 회원 데이터 조회
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with Kakao ID: " + kakaoId));
    }

}
