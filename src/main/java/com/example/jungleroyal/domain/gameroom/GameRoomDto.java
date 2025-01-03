package com.example.jungleroyal.domain.gameroom;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
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
    private int maxGameTime; // 분
    private String map;
    private int currentPlayers;
    private Boolean secret;
    private RoomStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String hostId;
    private String gameUrl;


    public static GameRoomDto updateRoomFromRequest(GameRoomRequest request, Long roomId) {
        return GameRoomDto.builder()
                .id(roomId)
                .title(request.getTitle())
                .minPlayers(request.getMinPlayers())
                .maxPlayers(request.getMaxPlayers())
                .maxGameTime(request.getMaxGameTime())
                .map(request.getMap())
                .secret(request.getSecret())
                .build();
    }

    public static GameRoomDto fromRequest(GameRoomRequest request, String hostId) {
        return GameRoomDto.builder()
                .title(request.getTitle())
                .minPlayers(request.getMinPlayers())
                .maxPlayers(request.getMaxPlayers())
                .maxGameTime(request.getMaxGameTime())
                .map(request.getMap())
                .secret(request.getSecret())
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
                .gameUrl(request.getGameUrl())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
