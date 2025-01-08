package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveRoomRequest {
    private String roomId;
    private String clientId;
}
