package com.example.jungleroyal.common.types;

public enum GameRoomStatus {
    GAME_ALREADY_STARTED("게임방이 이미 시작함."),
    GAME_ROOM_FULL("게임방이 꽉참."),
    GAME_ROOM_NOT_FOUND("게임방이 없음."),
    GAME_JOIN_AVAILABLE("게임 입장 가능.");

    private final String message;

    GameRoomStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
