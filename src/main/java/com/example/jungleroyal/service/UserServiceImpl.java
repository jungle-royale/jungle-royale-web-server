package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import com.example.jungleroyal.domain.*;
import com.example.jungleroyal.domain.dto.UserDto;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.RefreshTokenRepository;
import com.example.jungleroyal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RandomNicknameGenerator randomNicknameGenerator;

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).isPresent();
    }

    public String getKakaoIdByUserId(String userId){
        Long id = Long.parseLong(userId);
        UserJpaEntity userJpaEntity = userRepository.findById(id).orElse(new UserJpaEntity());
        return userJpaEntity.getKakaoId();

    }

    private void saveRefreshToken(UserJpaEntity userJpaEntity, String token, Integer expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserJpaEntity(userJpaEntity);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);

        refreshTokenRepository.save(refreshToken);
    }

    public UserDto getUserByKakaoId(String kakaoId) {
        UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Kakao ID: " + kakaoId));

        return UserDto.builder()
                .id(userJpaEntity.getId())
                .kakaoId(userJpaEntity.getKakaoId())
                .username(userJpaEntity.getUsername())
                .createdAt(userJpaEntity.getCreatedAt())
                .updatedAt(userJpaEntity.getUpdatedAt())
                .lastLoginAt(userJpaEntity.getLastLoginAt())
                .build();
    }

    public void kakaoUserJoin(String kakaoId, OAuthKakaoToken oAuthKakaoToken) {
        // 유저가 존재하지않으면 회원, 리프레시 토큰 저장
        if (!isUserExistsByKakaoId(kakaoId)) {
            String username = randomNicknameGenerator.generate();
            UserJpaEntity savedUserJpaEntity = saveKakaoUser(kakaoId, username);

            System.out.println("savedUser = " + savedUserJpaEntity);
            saveRefreshToken(savedUserJpaEntity, oAuthKakaoToken.getRefresh_token(), oAuthKakaoToken.getRefresh_token_expires_in());
            // 유저 등록 및 refreshtoken 등록
        } else {
            // 유저가 존재해 그럼 리프레시 토큰만 저장
            // refreshtoken 등록
            UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                    .orElse(new UserJpaEntity());
            saveRefreshToken(userJpaEntity, oAuthKakaoToken.getRefresh_token(), oAuthKakaoToken.getRefresh_token_expires_in());
        }
    }

    /**
     * 비회원 유저 생성
     * @return
     */
    public UserJpaEntity registerGuest() {String randomNickname = randomNicknameGenerator.generate();
        UserJpaEntity gueutUserJpaEntity = UserJpaEntity.createGueutUser(randomNickname);

        return userRepository.save(gueutUserJpaEntity);
    }

    /**
     * 회원인지 비회원인지 판단
     * @param kakaoId
     * @return 회원 - 기존 유저 정보 호출, 비회원 - 임시 회원 정보 생성
     */
    public UserJpaEntity findOrRegisterGuest(String kakaoId) {
        if (kakaoId == null || kakaoId.isEmpty()) {
            // 비회원 처리
            return registerGuest();
        }

        // 회원 데이터 조회
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with Kakao ID: " + kakaoId));
    }

    private UserJpaEntity saveKakaoUser(String kakaoId, String username) {
        UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                .orElse(new UserJpaEntity());
        return userRepository.save(userJpaEntity.createKakaoUser(kakaoId, username));
    }

    public String getUsernameById(String userId) {
        return userRepository.findUsernameById(Long.parseLong(userId));
    }
}
