package com.example.jungleroyal.common.exceptions;

public class UserAlreadyInGameException extends RuntimeException{
    public UserAlreadyInGameException(String message) {
        super(message);
    }
}
