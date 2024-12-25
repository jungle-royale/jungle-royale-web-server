package com.example.oauthlogin.common.exceptions;

public class RoomNotFoundException extends GameServerException{
    public RoomNotFoundException(Long roomId){
        super("존재하지 않는 방입니다. Room ID: " + roomId);
    }

}
