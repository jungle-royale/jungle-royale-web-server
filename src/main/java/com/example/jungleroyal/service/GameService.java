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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;

//    /**
//     * 게임 종료 처리
//     *
//     * @param roomUrl  종료할 방 URL
//     * @param rankings 게임 참여자들의 순위 및 clientId 리스트
//     */
    @Transactional
    public void endGame(EndGameRequest endGameRequest) {
        String roomId = endGameRequest.getRoomId();
        List<EndGameUserInfo> users = endGameRequest.getUsers();

        // 1. 방 정보 조회
        GameRoomJpaEntity gameRoom = gameRoomRepository.findByGameUrl(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        if (gameRoom.isEnd()) {
            throw new IllegalStateException("이미 종료 처리된 방입니다.");
        }

        // 2. 유저 정보 조회
        List<String> clientIds = users.stream()
                .map(EndGameUserInfo::getClientId)
                .collect(Collectors.toList());

        List<UserJpaEntity> participants = userRepository.findAllByClientIds(clientIds);

        // 3. 게임머니 지급 및 상태 초기화
        Map<String, Integer> clientIdToRank = users.stream()
                .collect(Collectors.toMap(EndGameUserInfo::getClientId, EndGameUserInfo::getRank));

        participants.forEach(user -> {
            int rank = clientIdToRank.getOrDefault(user.getClientId(), -1);
            if (rank == 1) {
                user.setGameMoney(user.getGameMoney() + 10000); // 1등 보상
            }
            // 유저 상태 및 필드 초기화
            user.setStatus(UserStatus.WAITING);
            user.setCurrentGameUrl(null);
            user.setClientId(null);
            user.setUpdatedAt(TimeUtils.createUtc());
        });

        userRepository.saveAll(participants);

        // 4. 방 상태 변경 및 인원수 초기화
        gameRoom.setStatus(RoomStatus.END);
        gameRoom.setCurrentPlayers(0);
        gameRoom.setUpdatedAt(TimeUtils.createUtc());

        gameRoomRepository.save(gameRoom);
    }


    public void participateInGame(UserJpaEntity userJpaEntity){
        if (userJpaEntity.getRole() == UserRole.MEMBER){
            System.out.println("Member user " + userJpaEntity.getUsername() + " has joined the game!");
        } else {
            System.out.println("Guest user " + userJpaEntity.getUsername() + " has joined the game!");
        }
    }

    @Transactional
    public void leaveRoom(LeaveRoomRequest leaveRoomRequest) {
        String roomId = leaveRoomRequest.getRoomId();
        String clientId = leaveRoomRequest.getClientId();

        // 1. 방 조회
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        // 2. 유저 조회
        UserJpaEntity user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for clientId: " + clientId));

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
    }
}
