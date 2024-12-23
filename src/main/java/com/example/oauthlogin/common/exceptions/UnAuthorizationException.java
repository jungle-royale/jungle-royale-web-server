package com.example.oauthlogin.common.exceptions;

public class UnAuthorizationException extends JungleRoyalException{
    public UnAuthorizationException(String message) {
        super(message);
    }

    public UnAuthorizationException(Throwable cause) {
        super(cause);
    }

    public UnAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
