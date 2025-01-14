package com.example.jungleroyal.domain.gameroom;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRoomJoinResponse {
    private Long roomId;
    private String clientId;
    private String username;

    public static GameRoomJoinResponse create(Long roomId, String clientId, String username) {
        return GameRoomJoinResponse.builder()
                .roomId(roomId)
                .clientId(clientId)
                .username(username)
                .build();

    }
}
