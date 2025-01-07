package com.example.jungleroyal.common.util;

import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;

public class RoomValidator {
    public static void validateRoomNotEnded(GameRoomJpaEntity room) {
        if (room.getStatus() == RoomStatus.END) {
            throw new GameRoomException("GAME_ROOM_ENDED", "이미 종료된 방입니다.");
        }
    }

    public static void validateRoomNotEmpty(GameRoomJpaEntity room) {
        if (room.getCurrentPlayers() == 0) {
            throw new GameRoomException("NO_PLAYERS_IN_ROOM", "방에 플레이어가 없어 입장할 수 없습니다.");
        }
    }

    public static void validateRoomCapacity(GameRoomJpaEntity room) {
        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new GameRoomException("GAME_ROOM_FULL", "방 정원이 초과되었습니다.");
        }
    }

    public static void validateRoomNotRunning(GameRoomJpaEntity room) {
        if (room.getStatus() == RoomStatus.RUNNING){
            throw new GameRoomException("GAME_ALREADY_STARTED", "게임이 이미 시작되었습니다.");
        }
    }

    public static void validateUserNotInGameDuringWaiting(GameRoomJpaEntity room, UserJpaEntity user) {
        if (room.getStatus() == RoomStatus.WAITING && user.getStatus() == UserStatus.IN_GAME) {
            throw new GameRoomException("INVALID_USER_STATE", "방이 대기중이나 유저가 현재 [IN_GAME] 상태입니다.");
        }
    }
}
