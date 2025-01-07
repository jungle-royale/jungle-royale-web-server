package com.example.jungleroyal.common.exception;

import lombok.Getter;

@Getter
public class GameRoomException extends RuntimeException{
    private final String errorCode;

    public GameRoomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
