package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.UserAlreadyInGameException;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.HashUtil;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.*;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.*;
import com.example.jungleroyal.service.repository.InventoryRepository;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final InventoryRepository inventoryRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserJpaEntity getUserJpaEntityById(Long userId){
        // UserJpaEntity 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).isPresent();
    }


    public UserDto getUserByKakaoId(String kakaoId) {
        UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Kakao ID: " + kakaoId));
        return UserJpaEntity.toDto(userJpaEntity);
    }

    public UserDto kakaoUserJoin(String kakaoId) {
        String username = randomNicknameGenerator.generate();
        UserJpaEntity savedUserJpaEntity = saveKakaoUser(kakaoId, username);

        return UserJpaEntity.toDto(savedUserJpaEntity);

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

    public String getClientId() {
        String key = HashUtil.encryptWithUUIDAndHash();
        return HashUtil.hash(key);
    }
    /**
     * 유저의 clientId와 gameRoomUrl을 갱신
     *
     * @param userId       갱신할 유저의 ID
     * @param clientId     새로 설정할 clientId
     * @param gameRoomUrl  새로 설정할 gameRoomUrl
     * @exception UserAlreadyInGameException 유저가 이미 게임중이거나 참여한 게임이 아직 끝나지 않았다.
     */
    @Transactional
    public void updateUserConnectionDetails(long userId, String gameRoomUrl, String clientId) {
        UserJpaEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 유저 상태 확인
        if (user.getUserStatus() == UserStatus.IN_GAME) {
            throw new UserAlreadyInGameException("User is already in a game. Game URL: " + user.getCurrentGameUrl());
        }

        // 필드값 갱신
        user.setUpdatedAt(TimeUtils.createUtc());
        user.setCurrentGameUrl(gameRoomUrl);
        user.setClientId(clientId);

        userRepository.save(user);
    }

    /**
     * 게임 시작 시 유저 상태를 IN_GAME으로 변경
     *
     * @param clientIds 게임에 참가할 유저 ID 목록
     */
    @Transactional
    public void updateUsersToInGame(List<String> clientIds) {
        List<UserJpaEntity> users = userRepository.findAllByClientIds(clientIds);

        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found for the given IDs");
        }

        users.forEach(user -> {
            if (user.getUserStatus() != UserStatus.WAITING) {
                throw new IllegalStateException("User is not in WAITING status: " + user.getId());
            }
            user.setUserStatus(UserStatus.IN_GAME);
            user.setUpdatedAt(LocalDateTime.now());
        });

        userRepository.saveAll(users);
        log.info("Updated users to IN_GAME: {}", clientIds);
    }

    /**
     * 게임 실패 시 clientId 기반으로 유저 상태를 WAITING으로 복구
     *
     * @param clientIds 상태를 복구할 유저 clientId 목록
     */
    @Transactional
    public void revertUsersToWaitingByClientIds(List<String> clientIds) {
        List<UserJpaEntity> users = userRepository.findAllByClientIds(clientIds);

        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found for the given clientIds");
        }

        users.forEach(user -> {
            if (user.getUserStatus() != UserStatus.IN_GAME) {
                throw new IllegalStateException("User is not in IN_GAME status: " + user.getClientId());
            }
            user.setUserStatus(UserStatus.WAITING);
            user.setUpdatedAt(LocalDateTime.now());
        });

        userRepository.saveAll(users);
        log.info("Reverted users to WAITING by clientIds: {}", clientIds);
    }
}
