package com.example.jungleroyal.service;


import com.example.jungleroyal.common.exceptions.DuplicateRoomException;
import com.example.jungleroyal.common.exceptions.GameRoomException;
import com.example.jungleroyal.common.exceptions.RoomByGameUrlFoundException;
import com.example.jungleroyal.common.exceptions.RoomNotFoundException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.util.EncryptionUtil;
import com.example.jungleroyal.common.util.HashUtil;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.repository.GameRoomJpaEntity;
import com.example.jungleroyal.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    // TODO: 의존성 문제 해결 -> redisTemplate를 감싸서 우리만의 클래스를 만들고, 우리 비즈니스 로직에 의존하도록 만들어야함.

    @Transactional
    public GameRoomDto createRoom(GameRoomDto gameRoomDto) {
        String lockKey = "host:" + gameRoomDto.getHostId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 락을 획득. 최대 대기 시간 5초, 락 보유 시간 10초
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                // 호스트 중복 확인
                if (gameRoomRepository.existsByHostId(gameRoomDto.getHostId())) {
                    throw new DuplicateRoomException("호스트가 이미 방을 생성했습니다. Host ID: " + gameRoomDto.getHostId());
                }
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

    
    public Optional<GameRoomDto> getRoomById(Long roomId) {
        log.info("UserInfoUsingRoomListResponse 객체 생성 완료  :" + System.currentTimeMillis());
        return gameRoomRepository.findById(roomId).map(GameRoomJpaEntity::toDto);
    }

    public GameRoomDto getRoomByIdOrThrow(Long roomId) {
        return gameRoomRepository.findById(roomId)
                .map(GameRoomJpaEntity::toDto)
                .orElseThrow(() -> new RoomNotFoundException("id",roomId));
    }

    
    @Transactional(readOnly = true)
    public GameRoomStatus checkRoomAvailability(Long roomId) {
        Optional<GameRoomJpaEntity> optionalRoom = gameRoomRepository.findById(roomId);
        if (optionalRoom.isEmpty()) {
            throw new GameRoomException("ROOM_NOT_FOUND", "존재하지 않는 방입니다.");
        }

        GameRoomJpaEntity room = optionalRoom.get();

        if (room.getStatus() == RoomStatus.RUNNING) {
            throw new GameRoomException("GAME_ALREADY_STARTED", "게임이 이미 시작되었습니다.");
        }

        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new GameRoomException("GAME_ROOM_FULL", "방 정원이 초과되었습니다.");
        }

        // 입장 가능
        return GameRoomStatus.GAME_JOIN_AVAILABLE;
    }

    
    public String getRoomClientIdByUserId(String userId) {
        return EncryptionUtil.encrypt(userId);
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
    }


}
