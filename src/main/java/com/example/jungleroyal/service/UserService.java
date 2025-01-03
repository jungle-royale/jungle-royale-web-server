package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import com.example.jungleroyal.domain.*;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.*;
import com.example.jungleroyal.service.repository.InventoryRepository;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final InventoryRepository inventoryRepository;

    public UserJpaEntity getUserJpaEntityById(Long userId){
        // UserJpaEntity 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).isPresent();
    }

    private void saveRefreshToken(UserJpaEntity userJpaEntity, String token, Integer expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserJpaEntity(userJpaEntity);
        refreshToken.setToken(token);
        refreshToken.setRefreshToken(token);
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
    public UserJpaEntity registerGuest() {
        String randomNickname = randomNicknameGenerator.generate();
        UserJpaEntity gueutUserJpaEntity = UserJpaEntity.createGueutUser(randomNickname);

        return userRepository.save(gueutUserJpaEntity);
    }

    public UserJpaEntity saveKakaoUser(String kakaoId, String username) {
        UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                .orElse(new UserJpaEntity());
        UserJpaEntity savedUser = userRepository.save(userJpaEntity.createKakaoUser(kakaoId, username));


        // 회원 가입 후 인벤토리 생성
        InventoryJpaEntity inventory = InventoryJpaEntity.builder()
                .user(savedUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        return savedUser;
    }
    
    public String getUsernameById(String userId) {
        return userRepository.findUsernameById(Long.parseLong(userId));
    }

    public void updateNickName(UserDto userDto) {
        // 사용자 정보 조회
        UserJpaEntity userJpaEntity = getUserJpaEntityById(userDto.getId());

        userJpaEntity.setUsername(userDto.getUsername());

        // 데이터베이스에 저장
        userRepository.save(userJpaEntity);
    }
}
