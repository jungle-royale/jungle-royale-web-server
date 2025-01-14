package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StartGameRequest {
    private String roomId; //😎 long으로 변경
    private List<String> clientIds;
}
