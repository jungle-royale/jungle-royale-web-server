package com.example.jungleroyal.common.exception;

public class RoomNotFoundException extends GameServerException{
    public RoomNotFoundException(String identifierType, String identifier) {
        super(String.format("Room not found for %s: %s", identifierType, identifier));
    }

    public RoomNotFoundException(String identifierType, Long identifier) {
        super(String.format("Room not found for %s: %d", identifierType, identifier));
    }

}
