package com.example.jungleroyal.service;


import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.exception.RoomByGameUrlFoundException;
import com.example.jungleroyal.common.exception.RoomNotFoundException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.*;
import com.example.jungleroyal.domain.game.GameReturnResponse;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.domain.gameroom.GameRoomJoinResponse;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public GameRoomJoinResponse joinGameRoom(Long roomId, String jwt) {
        String userId;

        // 유저 확인 (JWT 또는 게스트)
        if (jwt == null) {
            UserJpaEntity guestUser = userService.registerGuest();
            userId = String.valueOf(guestUser.getId());
        } else {
            userId = securityUtil.getUserId();
        }

        UserDto user = userService.getUserDtoById(Long.parseLong(userId));

        // 방 정보 확인 및 검증
        GameRoomJpaEntity room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다."));

        RoomValidator.validateRoomCapacity(room); // 방 정원 초과 확인
        RoomValidator.validateRoomNotEnded(room); // 방 종료 여부 확인

        // 유저 정보 업데이트
        String roomUrl = room.getGameUrl();
        String clientId = user.getClientId();

        // 같은 방이 아닌 경우 clientId 갱신
        if (!roomUrl.equals(user.getCurrentGameUrl())) {
            clientId = userService.getClientId(); // 새로운 clientId 생성
            userService.updateUserConnectionDetails(Long.parseLong(userId), roomUrl, clientId, false);
        }

        // 응답 생성
        return GameRoomJoinResponse.builder()
                .roomId(room.getId())
                .clientId(clientId)
                .username(user.getUsername())
                .build();

    }

    @Transactional
    public GameRoomDto createRoom(GameRoomDto gameRoomDto) {
        // 방 생성
        String gameUrl = HashUtil.encryptWithUUIDAndHash();
        gameRoomDto.setGameUrl(gameUrl);
        // TODO : gameRoomJpaEntity 의 세팅 메소드 생성 필요
        GameRoomJpaEntity gameRoomJpaEntity = GameRoomJpaEntity.fromDto(gameRoomDto);
        gameRoomJpaEntity.setCurrentPlayers(1);
        gameRoomJpaEntity.setCreatedAt(TimeUtils.createUtc());
        gameRoomJpaEntity.setUpdatedAt(TimeUtils.createUtc());
        gameRoomJpaEntity.setStatus(RoomStatus.WAITING);
        GameRoomJpaEntity savedRoom = gameRoomRepository.save(gameRoomJpaEntity);
        return GameRoomDto.fromGameRoomJpaEntity(savedRoom);

    }

    @Transactional
    public void updateRoom(GameRoomDto gameRoomDto) {
        gameRoomDto.setUpdatedAt(LocalDateTime.now());
        gameRoomRepository.save(GameRoomJpaEntity.fromDto(gameRoomDto));
    }

    @Transactional
    public void updateRoomStatus(Long roomId, RoomStatus status) {
        GameRoomJpaEntity room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        room.setUpdatedAt(LocalDateTime.now());
        gameRoomRepository.save(room);
    }

    public void deleteRoom(String userId) {

        GameRoomJpaEntity room = gameRoomRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RoomByGameUrlFoundException(userId));
        gameRoomRepository.delete(room);
        log.info("✅게임룸 삭제 완료 : {}" , userId);
    }

    @Transactional
    public List<GameRoomDto> listAllRooms() {
        log.info("(DB) 룸 리스트 조회 시작 :" + System.currentTimeMillis());
        return gameRoomRepository.findAll()
                .stream()
                .map(GameRoomJpaEntity::toDto)
                .toList();
    }

    @Transactional
    public List<GameRoomDto> listOfShowableRoom() {
        log.info("(DB) 룸 리스트 조회 시작 :" + System.currentTimeMillis());
        return gameRoomRepository.findAll()
                .stream()
                .filter(room -> {
                    return room.canShow();
                })
                .map(GameRoomJpaEntity::toDto)
                .toList();
    }

    public GameRoomDto getRoomByIdOrThrow(Long roomId) {
        return gameRoomRepository.findById(roomId)
                .map(GameRoomJpaEntity::toDto)
                .orElseThrow(() -> new RoomNotFoundException("id",roomId));
    }

    @Transactional(readOnly = true)
    public GameRoomStatus checkRoomAvailability(Long roomId, String userId) {
        GameRoomJpaEntity room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다."));

        UserJpaEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다."));

        RoomValidator.validateRoomNotEnded(room);

        // 방 상태가 RUNNING인 경우 같은 방에서 나왔으면 입장 가능
        if (room.getStatus() == RoomStatus.RUNNING && room.getGameUrl().equals(user.getCurrentGameUrl()) && room.getCurrentPlayers() != 0 && room.getCurrentPlayers() < room.getMaxPlayers()) {
            return GameRoomStatus.GAME_JOIN_AVAILABLE;
        }

        RoomValidator.validateUserNotInGameDuringWaiting(room, user);
        RoomValidator.validateRoomNotRunning(room);
        RoomValidator.validateRoomCapacity(room);
        RoomValidator.validateRoomNotEmpty(room);
        // 입장 가능
        return GameRoomStatus.GAME_JOIN_AVAILABLE;
    }

    public void deleteRoomById(Long id) {
        GameRoomJpaEntity gameRoomJpaEntity = gameRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        gameRoomRepository.delete(gameRoomJpaEntity);
    }

    public String getRoomUrlById(Long roomId) {
        return gameRoomRepository.getGameUrlById(roomId);
    }

    public void updateRoomStatusByRoomUrl(Long roomId, RoomStatus roomStatus) { //😎 수정 대상
        GameRoomJpaEntity room = gameRoomRepository.findById(roomId) //😎 수정 대상
            .orElseThrow(() -> new RoomNotFoundException("Room not found for roomId: ",roomId));
        room.setStatus(roomStatus);
        room.setUpdatedAt(TimeUtils.createUtc());

        gameRoomRepository.save(room);
    }

    /**
     * 1시간마다 currentPlayers가 0이고 RoomStatus가 WAITING인 방을 삭제
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24) // 1일 간격
    @Transactional
    public void cleanUpEmptyRooms() {
        log.info("빈 방을 조회중...");

        // 조건에 맞는 빈 방 조회
        List<GameRoomJpaEntity> emptyRooms = gameRoomRepository.findAllByStatusAndCurrentPlayers(RoomStatus.WAITING, 0);

        if (emptyRooms.isEmpty()) {
            log.info("빈 방이 없습니다.");
            return;
        }

        // 빈 방 삭제
        gameRoomRepository.deleteAll(emptyRooms);
        log.info("{} 개의 방을 삭제했습니다.", emptyRooms.size());
    }

    /**
     * 유저가 게임방에서 나갈 때, 대기 상태(WAITING)일 경우에만 참가 인원을 감소시킵니다.
     *
     * @param gameUrl 방의 고유 식별자인 gameUrl
     * @throws GameRoomException 방을 찾을 수 없을 때 발생
     * @throws IllegalStateException 참가자가 없는 방에서 호출되었을 때 발생
     */
    @Transactional
    public void handlePlayerLeave(String gameUrl) {
        // 방 정보를 gameUrl로 조회
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(gameUrl)
                .orElseThrow(() -> new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다."));

        // WAITING 상태에서만 참가 인원 감소
        if (room.getStatus() == RoomStatus.WAITING) {
            if (room.getCurrentPlayers() > 0) {
                room.setCurrentPlayers(room.getCurrentPlayers() - 1);
                room.setUpdatedAt(LocalDateTime.now());
                gameRoomRepository.save(room);
            } else {
                throw new IllegalStateException("참가자가 없는 방에서 나갈 수 없습니다.");
            }
        }
        // RUNNING 또는 END 상태에서는 참가 인원을 줄이지 않음
    }

    /**
     *
     * @return
     */
    public GameRoomDto getRoomByGameUrl(String gameUrl) {
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(gameUrl)
                .orElseThrow(() -> new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다."));

        return GameRoomDto.fromGameRoomJpaEntity(room);
    }


    private void isRoomEnd(GameRoomDto gameRoomDto) {
        if (gameRoomDto.getStatus() == RoomStatus.END) {
            throw new GameRoomException("GAME_ROOM_ENDED", "이미 종료된 방입니다.");
        }
    }

    public GameReturnResponse returnGame(String userId) {

        UserDto user = userService.getUserDtoById(Long.parseLong(userId));

        log.info("✅ 리턴하는 유저 닉네임 : {}", user.getUsername());

        if (user.getUserStatus() != UserStatus.IN_GAME) {
            throw new GameRoomException("USER_NOT_IN_GAME", "유저가 게임에 참여 중이 아니므로 다시 돌아갈 수 없습니다.");
        }

        String currentGameUrl = user.getCurrentGameUrl();

        GameRoomDto gameRoomDto  = getRoomByGameUrl(currentGameUrl);

        isRoomEnd(gameRoomDto);

        return GameReturnResponse.create(gameRoomDto.getId(), user.getClientId());

    }
}
