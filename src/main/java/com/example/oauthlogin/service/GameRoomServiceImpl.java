package com.example.oauthlogin.service;


import com.example.oauthlogin.common.exceptions.DuplicateRoomException;
import com.example.oauthlogin.common.exceptions.RoomByGameUrlFoundException;
import com.example.oauthlogin.common.exceptions.RoomNotFoundException;
import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.gameroom.GameRoom;
import com.example.oauthlogin.domain.gameroom.GameRoomDto;
import com.example.oauthlogin.domain.gameroom.GameRoomJpaEntity;
import com.example.oauthlogin.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    // TODO: 의존성 문제 해결 -> redisTemplate를 감싸서 우리만의 클래스를 만들고, 우리 비즈니스 로직에 의존하도록 만들어야함.
    private final RedissonClient redissonClient;

    @Override
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
                String hashValue = UUID.randomUUID().toString();
                gameRoomDto.setGameUrl(hashValue);

                GameRoomJpaEntity gameRoomJpaEntity = GameRoomJpaEntity.fromDto(gameRoomDto);
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

        // Redis에서 중복 확인
//        Boolean isHostExists = redisTemplate.hasKey(hostKey);
//        if(Boolean.TRUE.equals(isHostExists)){
//            throw new DuplicateRoomException("호스트가 이미 방을 생성했습니다. Host ID: " + gameRoomDto.getHostId());
//        }
//
//        // Redis에 호스트 등록 (TTL: 1시간)
//        redisTemplate.opsForValue().set(hostKey, "active", , TimeUnit.HOURS);

//        String gameUrl = UUID.randomUUID().toString();
//
//        gameRoomDto.setGameUrl(gameUrl);
//
//        GameRoomJpaEntity gameRoomJpaEntity = GameRoomJpaEntity.fromDto(gameRoomDto);
//        GameRoomJpaEntity savedRoom = gameRoomRepository.save(gameRoomJpaEntity);
//        return GameRoomDto.fromGameRoomJpaEntity(savedRoom);
    }

    @Override
    @Transactional
    public void updateRoom(GameRoomDto gameRoomDto) {
        gameRoomRepository.save(GameRoomJpaEntity.fromDto(gameRoomDto));
    }


    @Override
    @Transactional
    public void updateRoomStatus(Long roomId, RoomStatus status) {
        GameRoomJpaEntity room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        gameRoomRepository.save(room);
    }

    @Override
    public void deleteRoom(String gameUrl) {
        GameRoomJpaEntity room = gameRoomRepository.findByGameUrl(gameUrl)
                .orElseThrow(() -> new RoomByGameUrlFoundException(gameUrl));
        gameRoomRepository.delete(room);
    }

    @Override
    @Transactional
    public List<GameRoomDto> listAllRooms() {
        return gameRoomRepository.findAll()
                .stream()
                .map(GameRoomJpaEntity::toDto)
                .toList();
    }

    @Override
    public Optional<GameRoomDto> getRoomById(Long roomId) {
        return gameRoomRepository.findById(roomId).map(GameRoomJpaEntity::toDto);
    }

    public GameRoomDto getRoomByIdOrThrow(Long roomId) {
        return gameRoomRepository.findById(roomId)
                .map(GameRoomJpaEntity::toDto)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }
}
