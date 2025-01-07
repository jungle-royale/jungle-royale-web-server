package com.example.jungleroyal.common.exception;

public class JungleRoyalException extends RuntimeException{
    public JungleRoyalException(String message) {
        super(message);
    }

    public JungleRoyalException(Throwable cause) {
        super(cause);
    }

    public JungleRoyalException(String message, Throwable cause) {
        super(message, cause);
    }
}
