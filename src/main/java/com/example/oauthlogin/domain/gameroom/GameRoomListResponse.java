package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoomListResponse {
    private Long id;
    private String title;
    private int maxPlayers;
    private String gameUrl;
    private RoomStatus status;
    private int currentPlayers;
    // TODO : 추후 프론트엔드에서 필요하면 보냄
//    private Boolean secret;
//    private String map;
//    private int maxGameTime;

    public static GameRoomListResponse fromDto(GameRoomDto gameRoomDto) {
        return GameRoomListResponse.builder()
                .id(gameRoomDto.getId())
                .title(gameRoomDto.getTitle())
                .maxPlayers(gameRoomDto.getMaxPlayers())
//                .maxGameTime(gameRoomDto.getMaxGameTime())
//                .map(gameRoomDto.getMap())
                .currentPlayers(gameRoomDto.getCurrentPlayers())
//                .secret(gameRoomDto.getSecret())
                .status(gameRoomDto.getStatus())
                .gameUrl(gameRoomDto.getGameUrl())
                .build();
    }
}
