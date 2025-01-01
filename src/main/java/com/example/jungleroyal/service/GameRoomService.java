package com.example.jungleroyal.service;

import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;

import java.util.List;
import java.util.Optional;

public interface GameRoomService {
    GameRoomDto createRoom(GameRoomDto gameRoomDto);
    void updateRoom(GameRoomDto roomDto);

    void updateRoomStatus(Long roomId, RoomStatus status);

    void deleteRoom(String gameUrl);

    List<GameRoomDto> listAllRooms();

    Optional<GameRoomDto> getRoomById(Long roomId);

    GameRoomDto getRoomByIdOrThrow(Long roomId);
    GameRoomStatus checkRoomAvailability(Long roomId);

    String getRoomClientIdByUserId(String userId);

    void deleteRoomById(Long id);

    String getRoomUrlById(Long roomId);
}
