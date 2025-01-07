package com.example.jungleroyal.common.exception;

public class RoomByGameUrlFoundException extends GameServerException {
    public RoomByGameUrlFoundException(String gameUrl){
        super("존재하지 않는 방입니다. gameUrl: " + gameUrl);
    }

}
