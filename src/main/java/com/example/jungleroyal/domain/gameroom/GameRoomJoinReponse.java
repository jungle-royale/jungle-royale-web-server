package com.example.jungleroyal.domain.gameroom;

import com.example.jungleroyal.domain.game.GameReturnResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRoomJoinReponse {
    private String roomId;
    private String clientId;
    private String username;

    public static GameRoomJoinReponse create(String currentGameUrl, String clientId, String username) {
        return GameRoomJoinReponse.builder()
                .roomId(currentGameUrl)
                .clientId(clientId)
                .username(username)
                .build();

    }
}
