package com.example.jungleroyal.domain.gameroom;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRoomJoinReponse {
    private String roomId;
    private String clientId;
}
