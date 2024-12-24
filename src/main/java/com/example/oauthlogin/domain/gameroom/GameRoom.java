package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoom {
    private Long id;
    private String title;
    private int minPlayers;
    private int maxPlayers;
    private int maxGameTime; // in minutes
    private String map;
    private Boolean secret;
    private int currentPlayers; // New field for tracking connected players
    private String hostId; // New field for tracking connected players
    private RoomStatus status = RoomStatus.WAITING;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GameRoomDto toDto() {
        return GameRoomDto.builder()
                .id(id)
                .title(title)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .maxGameTime(maxGameTime)
                .map(map)
                .currentPlayers(currentPlayers)
                .secret(secret)
                .status(status)
                .hostId(hostId)
                .build();
    }

    public static GameRoom fromDto(GameRoomDto dto) {
        return GameRoom.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .minPlayers(dto.getMinPlayers())
                .maxPlayers(dto.getMaxPlayers())
                .maxGameTime(dto.getMaxGameTime())
                .map(dto.getMap())
                .currentPlayers(dto.getCurrentPlayers())
                .secret(dto.getSecret())
                .status(dto.getStatus())
                .hostId(dto.getHostId())
                .build();
    }

    public void updateFromDto(GameRoomDto dto) {
        this.title = dto.getTitle();
        this.minPlayers = dto.getMinPlayers();
        this.maxPlayers = dto.getMaxPlayers();
        this.maxGameTime = dto.getMaxGameTime();
        this.map = dto.getMap();
        this.secret = dto.getSecret();
        this.currentPlayers = dto.getCurrentPlayers();
        this.status = dto.getStatus();
    }
}

