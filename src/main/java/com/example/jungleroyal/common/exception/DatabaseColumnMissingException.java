package com.example.jungleroyal.common.exception;

public class DatabaseColumnMissingException extends RuntimeException{
    public DatabaseColumnMissingException(String message) {
        super(message);
    }
}
