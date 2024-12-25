package com.example.oauthlogin.service;

import com.example.oauthlogin.common.types.UserRole;
import com.example.oauthlogin.common.util.RandomNicknameGenerator;
import com.example.oauthlogin.domain.*;
import com.example.oauthlogin.domain.dto.UserDto;
import com.example.oauthlogin.repository.RefreshTokenRepository;
import com.example.oauthlogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RandomNicknameGenerator randomNicknameGenerator;

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).isPresent();
    }

    public String getKakaoIdByUserId(String userId){
        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id).orElse(new User());
        return user.getKakaoId();

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
            String username = randomNicknameGenerator.generate();
            User savedUser = saveKakaoUser(kakaoId, username);

            System.out.println("savedUser = " + savedUser);
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

        String randomNickname = randomNicknameGenerator.generate();
        User gueutUser = User.createGueutUser(randomNickname);

        return userRepository.save(gueutUser);
    }

    /**
     * 회원인지 비회원인지 판단
     * @param kakaoId
     * @return 회원 - 기존 유저 정보 호출, 비회원 - 임시 회원 정보 생성
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

    private User saveKakaoUser(String kakaoId, String username) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElse(new User());
        return userRepository.save(user.createKakaoUser(kakaoId, username));
    }

    public String getUsernameById(String userId) {
        return userRepository.findUsernameById(Long.parseLong(userId));
    }
}
