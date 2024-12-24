package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoomResponse {
    private Long id;
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int maxGameTime; // in minutes
    private String map;
    private Boolean secret;
    private int currentPlayers; // New field for tracking connected players
    private Long hostId; // New field for tracking connected players
    private RoomStatus status;
    private String gameUrl;

    public static GameRoomResponse fromDto(GameRoomDto gameRoomDto) {
        return GameRoomResponse.builder()
                .id(gameRoomDto.getId())
                .title(gameRoomDto.getTitle())
                .minPlayers(gameRoomDto.getMinPlayers())
                .maxPlayers(gameRoomDto.getMaxPlayers())
                .maxGameTime(gameRoomDto.getMaxGameTime())
                .map(gameRoomDto.getMap())
                .secret(gameRoomDto.getSecret())
                .currentPlayers(gameRoomDto.getCurrentPlayers())
                .status(gameRoomDto.getStatus())
                .gameUrl(gameRoomDto.getGameUrl())
                .hostId(gameRoomDto.getHostId())
                .build();
    }
}
