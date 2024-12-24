package com.example.oauthlogin.common.exceptions;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(Long roomId){
        super("Room not found with id: " + roomId);
    }
}
