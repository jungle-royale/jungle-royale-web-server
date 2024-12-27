package com.example.jungleroyal.domain.gameroom;

import com.example.jungleroyal.common.types.RoomStatus;
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
}

