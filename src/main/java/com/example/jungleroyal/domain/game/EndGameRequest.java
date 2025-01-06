package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EndGameRequest {
    private String roomId;
    private List<String> rankings;
}
