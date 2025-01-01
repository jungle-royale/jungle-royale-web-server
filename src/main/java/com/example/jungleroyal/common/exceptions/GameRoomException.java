package com.example.jungleroyal.common.exceptions;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class GameRoomException extends RuntimeException{
    private final String errorCode;

    public GameRoomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
