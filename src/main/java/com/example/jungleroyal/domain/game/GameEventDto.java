package com.example.jungleroyal.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEventDto {
    private String playerName;
    private String action;
    private String timestamp;
}
