package com.example.jungleroyal.domain.gameroom;

import com.example.jungleroyal.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoomListResponse {
    private Long id;
    private String title;
    private int maxPlayers;
    private String gameUrl;
    private RoomStatus status;
    private int minPlayers;
    // TODO : 추후 프론트엔드에서 필요하면 보냄
//    private Boolean secret;
//    private String map;
//    private int maxGameTime;

    public static GameRoomListResponse fromDto(GameRoomDto gameRoomDto) {
        return GameRoomListResponse.builder()
                .id(gameRoomDto.getId())
                .title(gameRoomDto.getTitle())
                .maxPlayers(gameRoomDto.getMaxPlayers())
//                .maxGameTime(gameRoomDto.getMaxGameTime())
//                .map(gameRoomDto.getMap())
                .minPlayers(gameRoomDto.getMinPlayers())
//                .secret(gameRoomDto.getSecret())
                .status(gameRoomDto.getStatus())
                .gameUrl(gameRoomDto.getGameUrl())
                .build();
    }
}
