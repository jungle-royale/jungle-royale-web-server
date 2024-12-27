package com.example.jungleroyal.common.exceptions;

public class DuplicateRoomException extends GameServerException{
    public DuplicateRoomException(String message){
        super(message);
    }
}
