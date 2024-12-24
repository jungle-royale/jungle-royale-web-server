package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean secret;
    private RoomStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long hostId;
    private String hash;

    public static GameRoomDto fromRequest(GameRoomRequest request, Long hostId) {
        return GameRoomDto.builder()
                .title(request.getTitle())
                .minPlayers(request.getMinPlayers())
                .maxPlayers(request.getMaxPlayers())
                .maxGameTime(request.getMaxGameTime())
                .map(request.getMap())
                .secret(request.getSecret())
                .currentPlayers(request.getCurrentPlayers())
                .status(request.getStatus())
                .hostId(hostId)
                .build();
    }

    public static GameRoomDto fromGameRoomJpaEntity(GameRoomJpaEntity request) {
        return GameRoomDto.builder()
                .id(request.getId())
                .title(request.getTitle())
                .minPlayers(request.getMinPlayers())
                .maxPlayers(request.getMaxPlayers())
                .maxGameTime(request.getMaxGameTime())
                .map(request.getMap())
                .secret(request.getSecret())
                .currentPlayers(request.getCurrentPlayers())
                .status(request.getStatus())
                .hostId(request.getHostId())
                .hash(request.getHash())
                .build();
    }
}
