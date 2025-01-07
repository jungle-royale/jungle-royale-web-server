package com.example.jungleroyal.service;


import com.example.jungleroyal.common.exceptions.DuplicateRoomException;
import com.example.jungleroyal.common.exceptions.GameRoomException;
import com.example.jungleroyal.common.exceptions.RoomByGameUrlFoundException;
import com.example.jungleroyal.common.exceptions.RoomNotFoundException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.EncryptionUtil;
import com.example.jungleroyal.common.util.HashUtil;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.GameRoomRepository;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;

    @Transactional
    public GameRoomDto createRoom(GameRoomDto gameRoomDto) {
        String lockKey = "host:" + gameRoomDto.getHostId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 락을 획득. 최대 대기 시간 5초, 락 보유 시간 10초
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {

                // TODO: 호스트 중복을 확인할 게 아니라, 현재 호스트가 참여했던 게임이 끝났는지를 확인해야 함

                // 방 생성
                String gameUrl = HashUtil.encryptWithUUIDAndHash();
                gameRoomDto.setGameUrl(gameUrl);
                // TODO : gameRoomJpaEntity 의 세팅 메소드 생성 필요
                GameRoomJpaEntity gameRoomJpaEntity = GameRoomJpaEntity.fromDto(gameRoomDto);
                gameRoomJpaEntity.setCurrentPlayers(1);
                gameRoomJpaEntity.setCreatedAt(LocalDateTime.now());
                gameRoomJpaEntity.setUpdatedAt(LocalDateTime.now());
                gameRoomJpaEntity.setStatus(RoomStatus.WAITING);
                GameRoomJpaEntity savedRoom = gameRoomRepository.save(gameRoomJpaEntity);
                return GameRoomDto.fromGameRoomJpaEntity(savedRoom);
            } else {
                throw new IllegalStateException("락 획득 실패. 다른 요청이 이미 처리 중입니다.");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("락 처리 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }

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

    public void deleteRoom(String gameUrl) {
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(gameUrl)
                .orElseThrow(() -> new RoomByGameUrlFoundException(gameUrl));
        gameRoomRepository.delete(room);
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

        // 방 상태가 종료된 경우 예외 처리
        if (room.getStatus() == RoomStatus.END) {
            throw new GameRoomException("GAME_ROOM_ENDED", "이미 종료된 방입니다.");
        }

        // 방 상태가 RUNNING인 경우 같은 방에서 나왔으면 입장 가능
        if (room.getStatus() == RoomStatus.RUNNING && room.getGameUrl().equals(user.getCurrentGameUrl()) && room.getCurrentPlayers() != 0 && room.getCurrentPlayers() < room.getMaxPlayers()) {
            return GameRoomStatus.GAME_JOIN_AVAILABLE;
        }

        // 방 상태가 WAITING인데 유저 상태가 IN_GAME인 경우 예외 처리
        if (room.getStatus() == RoomStatus.WAITING && user.getStatus() == UserStatus.IN_GAME) {
            throw new GameRoomException("INVALID_USER_STATE", "방이 대기중이나 유저가 현재 [IN_GAME] 상태입니다.");
        }

        // 방 상태가 RUNNING인 경우 예외 처리
        if (room.getStatus() == RoomStatus.RUNNING) {
            throw new GameRoomException("GAME_ALREADY_STARTED", "게임이 이미 시작되었습니다.");
        }

        // 방 정원이 초과된 경우 예외 처리
        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new GameRoomException("GAME_ROOM_FULL", "방 정원이 초과되었습니다.");
        }

        // 방에 현재 플레이어가 없을 경우 예외 처리
        if (room.getCurrentPlayers() == 0) {
            throw new GameRoomException("NO_PLAYERS_IN_ROOM", "방에 플레이어가 없어 입장할 수 없습니다.");
        }
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

    public void updateRoomStatusByRoomUrl(String roomId, RoomStatus roomStatus) {
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(roomId)
            .orElseThrow(() -> new RoomNotFoundException("Room not found for URL: ",roomId));
        room.setStatus(roomStatus);
        room.setUpdatedAt(LocalDateTime.now());

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
}
