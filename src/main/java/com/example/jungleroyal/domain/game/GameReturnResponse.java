package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameReturnResponse {
    private String roomId;
    private String clientId;

    public static GameReturnResponse create(String roomId, String clientId){
        return GameReturnResponse.builder()
                .roomId(roomId)
                .clientId(clientId)
                .build();
    }
}
