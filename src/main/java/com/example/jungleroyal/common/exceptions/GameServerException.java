package com.example.jungleroyal.common.exceptions;

public class GameServerException extends RuntimeException{
    public GameServerException(String message) {
        super(message);
    }

    public GameServerException(String message, Throwable cause) {
        super(message, cause);
    }
}