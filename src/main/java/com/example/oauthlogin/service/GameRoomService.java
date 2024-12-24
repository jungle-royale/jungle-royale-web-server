package com.example.oauthlogin.service;

import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.gameroom.GameRoomDto;

import java.util.List;
import java.util.Optional;

public interface GameRoomService {
    GameRoomDto createRoom(GameRoomDto gameRoomDto);
    void updateRoom(GameRoomDto roomDto);

    void updateRoomStatus(Long roomId, RoomStatus status);

    void deleteRoom(Long roomId);

    List<GameRoomDto> listAllRooms();

    Optional<GameRoomDto> getRoomById(Long roomId);

    GameRoomDto getRoomByIdOrThrow(Long roomId);
}
