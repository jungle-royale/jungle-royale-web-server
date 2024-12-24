package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRoomRequest {
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int maxGameTime;
    private String map;
    private int currentPlayers;
    private Boolean secret;
    private RoomStatus status = RoomStatus.WAITING;
}
