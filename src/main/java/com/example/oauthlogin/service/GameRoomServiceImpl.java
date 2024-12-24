package com.example.oauthlogin.service;


import com.example.oauthlogin.common.exceptions.DuplicateRoomException;
import com.example.oauthlogin.common.exceptions.RoomNotFoundException;
import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.gameroom.GameRoom;
import com.example.oauthlogin.domain.gameroom.GameRoomDto;
import com.example.oauthlogin.domain.gameroom.GameRoomJpaEntity;
import com.example.oauthlogin.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomRepository gameRoomRepository;

    @Override
    @Transactional
    public GameRoomDto createRoom(GameRoomDto gameRoomDto) {
        String gameUrl = UUID.randomUUID().toString();

        gameRoomDto.setGameUrl(gameUrl);

        GameRoomJpaEntity gameRoomJpaEntity = GameRoomJpaEntity.fromDto(gameRoomDto);
        GameRoomJpaEntity savedRoom = gameRoomRepository.save(gameRoomJpaEntity);
        return GameRoomDto.fromGameRoomJpaEntity(savedRoom);
    }

    @Override
    @Transactional
    public void updateRoom(Long roomId, GameRoomDto gameRoomDto) {
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
    public void deleteRoom(Long roomId) {
        gameRoomRepository.deleteById(roomId);
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
