package com.example.jungleroyal.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEvent {
    private String playerName;
    private String action;
    private String timestamp;
}
