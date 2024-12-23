package com.example.oauthlogin.domain.dto;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRoomDto {
    private Long id;
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int maxGameTime;
    private String mapInfo;
    private int currentPlayers;
    private RoomStatus status;
}
