package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.exception.UserAlreadyInGameException;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.HashUtil;
import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.InventoryJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import com.example.jungleroyal.service.repository.InventoryRepository;
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
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final InventoryRepository inventoryRepository;
    private final GameRoomRepository gameRoomRepository;

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
        log.info("✅비회원 가입 완료, 가입 유저 닉네임 : {}", randomNickname);
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

    @Transactional
    public void updateNickName(UserDto userDto) {
        // 닉네임 중복 확인
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new GameRoomException("USERNAME_DUPLICATE", "이미 사용 중인 닉네임입니다: " + userDto.getUsername());
        }

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
     * 유저의 clientId와 gameRoomUrl을 갱신합니다.
     *
     * @param userId       갱신할 유저의 ID
     * @param gameRoomUrl  새로 설정할 gameRoomUrl
     * @param clientId     새로 설정할 clientId
     * @param isCreatingRoom 방 생성 여부 (true: 방 생성 시 처리, false: 일반 입장 시 처리)
     * @exception UserAlreadyInGameException 유저가 이미 게임중이거나 참여한 게임이 아직 끝나지 않았을 때 발생
     * @exception GameRoomException 방이 존재하지 않거나 정원이 초과된 경우 발생
     */

    @Transactional
    public void updateUserConnectionDetails(long userId, String gameRoomUrl, String clientId, boolean isCreatingRoom) {
        UserJpaEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(gameRoomUrl)
                .orElseThrow(() -> new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다."));

        // 유저 상태 확인
        if (user.getStatus() == UserStatus.IN_GAME) {
            throw new UserAlreadyInGameException("User is already in a game. Game URL: " + user.getCurrentGameUrl());
        }

        // 방 정원이 초과되지 않았는지 확인
        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new GameRoomException("GAME_ROOM_FULL", "방 정원이 초과되었습니다.");
        }

        // 방 상태가 RUNNING인 경우 같은 방에서 나왔으면 입장 가능
        if (!isCreatingRoom && !room.getGameUrl().equals(user.getCurrentGameUrl())) {
            room.setCurrentPlayers(room.getCurrentPlayers() + 1);
            room.setUpdatedAt(TimeUtils.createUtc());

            // 업데이트된 방 저장
            gameRoomRepository.save(room);
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
            if (user.getStatus() != UserStatus.WAITING) {
                throw new IllegalStateException("User is not in WAITING status: " + user.getId());
            }
            user.setStatus(UserStatus.IN_GAME);
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
            if (user.getStatus() != UserStatus.IN_GAME) {
                throw new IllegalStateException("User is not in IN_GAME status: " + user.getClientId());
            }
            user.setStatus(UserStatus.WAITING);
            user.setUpdatedAt(LocalDateTime.now());
        });

        userRepository.saveAll(users);
        log.info("Reverted users to WAITING by clientIds: {}", clientIds);
    }

    public String calculateUserRank(Long userId) {
        // 모든 유저를 가져와 점수 기준으로 정렬
        List<UserJpaEntity> allUsers = userRepository.findAll();
        allUsers.sort((u1, u2) -> u2.getScore().compareTo(u1.getScore())); // score 내림차순 정렬

        // 유저의 등수를 계산
        int rank = 1;
        int previousScore = -1;
        int actualRank = 1;

        for (UserJpaEntity user : allUsers) {

            // 점수가 0이면 "-"로 처리
            if (user.getId().equals(userId) && user.getScore() == 0) {
                return "-";
            }

            if (!user.getScore().equals(previousScore)) {
                actualRank = rank; // 점수가 달라지면 실제 등수 갱신
            }

            if (user.getId().equals(userId)) {
                // 100등 초과인 경우
                return actualRank > 100 ? "100등 이외" : String.valueOf(actualRank);
            }

            previousScore = user.getScore();
            rank++;
        }

        // 유저를 찾지 못한 경우
        throw new IllegalArgumentException("User not found");
    }

    public UserDto getUserDtoById(long userId) {
        return userRepository.findById(userId)
                .map(UserJpaEntity::toDto) // UserJpaEntity를 UserDto로 변환
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
