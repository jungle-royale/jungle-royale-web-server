package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

/**
 * 게임 생성 시 요청
 */
@Data
@Builder
public class GameRoomRequest {
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int maxGameTime;
    private String map;
    private Boolean secret;
}
