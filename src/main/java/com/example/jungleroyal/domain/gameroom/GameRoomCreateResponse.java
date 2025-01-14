package com.example.jungleroyal.domain.gameroom;

import lombok.Builder;
import lombok.Data;

/**
 * 게임 생성 response
 */
@Data
@Builder
public class GameRoomCreateResponse {
    private Long roomId;
    private String clientId;
    private String username;
}
