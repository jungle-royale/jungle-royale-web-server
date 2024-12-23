package com.example.oauthlogin.service;

import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.dto.GameRoomDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    GameRoomDto createRoom(GameRoomDto roomDto);
    GameRoomDto updateRoom(Long roomId, GameRoomDto roomDto);
    void updateRoomStatus(Long roomId, RoomStatus status);
    void deleteRoom(Long roomId);
    List<GameRoomDto> listAllRooms();
    Optional<GameRoomDto> getRoomById(Long roomId);
}
