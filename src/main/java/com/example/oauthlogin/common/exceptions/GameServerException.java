package com.example.oauthlogin.common.exceptions;

public class GameServerException extends RuntimeException{
    public GameServerException(String message) {
        super(message);
    }

    public GameServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
