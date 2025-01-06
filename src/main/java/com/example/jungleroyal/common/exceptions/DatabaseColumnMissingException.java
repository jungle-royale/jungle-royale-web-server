package com.example.jungleroyal.common.exceptions;

public class DatabaseColumnMissingException extends RuntimeException{
    public DatabaseColumnMissingException(String message) {
        super(message);
    }
}
