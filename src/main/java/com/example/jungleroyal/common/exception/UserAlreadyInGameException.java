package com.example.jungleroyal.common.exception;

public class UserAlreadyInGameException extends RuntimeException{
    public UserAlreadyInGameException(String message) {
        super(message);
    }
}
