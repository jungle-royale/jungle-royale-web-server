package com.example.jungleroyal.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameServerNotificationRequest {
    private String roomId;
    private int minPlayers;
    private int maxPlayTime; // seconds
    private String username;
}
