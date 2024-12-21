package com.example.oauthlogin.service;


import com.example.oauthlogin.domain.GameRoom;
import com.example.oauthlogin.domain.RoomStatus;
import com.example.oauthlogin.dto.GameRoomDto;
import com.example.oauthlogin.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameRoomService implements RoomService{
    private final GameRoomRepository gameRoomRepository;

    @Override
    public GameRoomDto createRoom(GameRoomDto roomDto) {
        GameRoom room = GameRoom.fromDto(roomDto);
        System.out.println("room = " + room);
        GameRoom savedRoom = gameRoomRepository.save(room);
        return savedRoom.toDto();
    }

    @Override
    public GameRoomDto updateRoom(Long roomId, GameRoomDto roomDto) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.updateFromDto(roomDto);
        return gameRoomRepository.save(room).toDto();
    }

    @Override
    public void updateRoomStatus(Long roomId, RoomStatus status) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        gameRoomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long roomId) {
        gameRoomRepository.deleteById(roomId);
    }

    @Override
    public List<GameRoomDto> listAllRooms() {
        return gameRoomRepository.findAll()
                .stream()
                .map(GameRoom::toDto)
                .toList();
    }

    @Override
    public Optional<GameRoomDto> getRoomById(Long roomId) {
        return gameRoomRepository.findById(roomId).map(GameRoom::toDto);
    }
}
