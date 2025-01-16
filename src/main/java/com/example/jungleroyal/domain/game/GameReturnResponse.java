package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameReturnResponse {
    private Long roomId;
    private String clientId;
    private String username;

    public static GameReturnResponse create(Long roomId, String clientId, String username){
        return GameReturnResponse.builder()
                .roomId(roomId)
                .clientId(clientId)
                .username(username)
                .build();
    }
}
