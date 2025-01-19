package com.example.jungleroyal.service;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.game.EndGameRequest;
import com.example.jungleroyal.domain.game.EndGameUserInfo;
import com.example.jungleroyal.domain.game.LeaveRoomRequest;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
//    private static final Set<String> ALLOWED_HOST_IDS = Set.of("3", "77", "330", "336", "847"); // 기프티콘 제공 룸 생성 전용 userId
    private static final Set<String> ALLOWED_HOST_IDS = Set.of("627"); // 기프티콘 제공 룸 생성 전용 userId

    @Transactional
    public void endGame(EndGameRequest endGameRequest) {
        log.info("✅ 게임 종료 객체 정보  : {}", endGameRequest);
        String specialUrl = "https://kko.kakao.com/51DBEqtzbl"; // 저장할 URL
        AtomicInteger highestScore = new AtomicInteger(Integer.MIN_VALUE);
        AtomicReference<UserJpaEntity> topScoringUser = new AtomicReference<>(null);

        String roomId = endGameRequest.getRoomId();
        List<EndGameUserInfo> users = endGameRequest.getUsers();

        // 1. 방 정보 조회
        GameRoomJpaEntity gameRoom = gameRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        if (gameRoom.isEnd()) {
            throw new IllegalStateException("이미 종료 처리된 방입니다.");
        }

        // 4. 방 상태 변경 및 인원수 초기화
        gameRoom.setStatus(RoomStatus.END);
        gameRoom.setCurrentPlayers(0);
        gameRoom.setUpdatedAt(TimeUtils.createUtc());

        log.info("✅ 방 리셋 정보 : status : {}, currentPlayers : {}", gameRoom.getStatus(), gameRoom.getCurrentPlayers());
        gameRoomRepository.save(gameRoom);

        // 2. 유저 정보 조회
        List<String> clientIds = users.stream()
                .map(EndGameUserInfo::getClientId)
                .collect(Collectors.toList());

        List<UserJpaEntity> participants = userRepository.findAllByClientIds(clientIds);
        log.info("✅ 유저 정보 : {}", participants);
        // 3. 게임머니 지급 및 상태 초기화
        Map<String, Integer> clientIdToRank = users.stream()
                .collect(Collectors.toMap(EndGameUserInfo::getClientId, EndGameUserInfo::getRank));

        participants.forEach(user -> {
            int rank = clientIdToRank.getOrDefault(user.getClientId(), -1);
            int kill = users.stream()
                    .filter(u -> u.getClientId().equals(user.getClientId()))
                    .mapToInt(EndGameUserInfo::getKill)
                    .findFirst()
                    .orElse(0);

            // 스코어 계산
            int score = calculateScore(rank, kill);
            user.setGameMoney(user.getGameMoney() + score); // 게임머니 추가
            user.setScore(user.getScore() + score); // 유저 스코어 추가

            // 누적 킬 수 업데이트
            user.setTotalKills(user.getTotalKills() + kill);

            // 1등 횟수 업데이트
            if (rank == 1) {
                user.setTotalFirstPlace(user.getTotalFirstPlace() + 1);
            }

            // 가장 높은 점수와 유저 갱신
            if (score > highestScore.get()) {
                highestScore.set(score);
                topScoringUser.set(user);
            }

            // 유저 상태 및 필드 초기화
            user.setStatus(UserStatus.WAITING);
            user.setCurrentGameUrl(null);
            user.setClientId(null);
            user.setUpdatedAt(TimeUtils.createUtc());
            log.info("✅ 유저 리셋 정보 -> 닉네임 : {}, 상태 : {}, GameUrl : {},  clientId : {}", user.getUsername(), user.getStatus(), user.getCurrentGameUrl(), user.getClientId());
        });


//        // 4. 가장 높은 점수를 가진 유저에게 URL 저장
//        if (topScoringUser.get() != null && ALLOWED_HOST_IDS.contains(gameRoom.getHostId())) {
//            topScoringUser.get().setGiftImageUrl(specialUrl); // URL 저장
//        }

        if (topScoringUser.get() != null){
            log.info("✅ 1등 유저 닉네임과 정보 -> 유저 닉네임 :{}, 유저 정보 : {}", topScoringUser.get().getUsername(), topScoringUser);
        }

        // 5. 변경된 유저 데이터 저장
        userRepository.saveAll(participants);
    }

    private int calculateScore(int rank, int kill) {
        int rankScore = Math.max(100 - rank * 10, 0); // 랭크 기반 점수
        int killScore = kill * 50; // 킬 수 기반 점수
        int baseScore = 50; // 기본 점수
        return rankScore + killScore + baseScore;
    }

    @Transactional
    public void leaveRoom(LeaveRoomRequest leaveRoomRequest) {
        String roomId = leaveRoomRequest.getRoomId();
        String clientId = leaveRoomRequest.getClientId();

        // 1. 방 조회
        GameRoomJpaEntity room = gameRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        // 2. 유저 조회
        UserJpaEntity user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for clientId: " + clientId));
        log.info("✅ 나가기 요청 유저 닉네임 : {}", user.getUsername());

        // 3. 유저가 해당 방에 속해 있는지 확인
        if (!room.getGameUrl().equals(user.getCurrentGameUrl())) {
            throw new IllegalStateException("유저가 해당 방에 속해 있지 않습니다.");
        }

        // 4. 참여 인원 감소 및 상태 초기화
        if (room.getCurrentPlayers() > 0) {
            room.setCurrentPlayers(room.getCurrentPlayers() - 1);
            room.setUpdatedAt(TimeUtils.createUtc());
            gameRoomRepository.save(room);
        }
        log.info("✅ 현재 게임룸 제목 : {}, 현재인원 : {}", room.getTitle(), room.getCurrentPlayers());
    }
}
