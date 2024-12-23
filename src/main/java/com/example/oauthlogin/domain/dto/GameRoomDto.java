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
    private String map;
    private int currentPlayers;
    private boolean secret;
    private RoomStatus status;
    private Long hostId;

    public static GameRoomDto fromRequest(GameRoomRequest request, Long hostId) {
        return GameRoomDto.builder()
                .title(request.getTitle())
                .minPlayers(request.getMinPlayers())
                .maxPlayers(request.getMaxPlayers())
                .maxGameTime(request.getMaxGameTime())
                .map(request.getMap())
                .secret(request.isSecret())
                .currentPlayers(request.getCurrentPlayers())
                .status(request.getStatus())
                .hostId(hostId)
                .build();
    }
}
