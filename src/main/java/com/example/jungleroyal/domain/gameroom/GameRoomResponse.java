package com.example.jungleroyal.domain.gameroom;

import com.example.jungleroyal.common.types.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoomResponse {
    private Long id;
    private String title;
    private int maxPlayers;
    private int currentPlayers;
    private RoomStatus status;
    // TODO : 방 생성시 게임에 입장하면 웹 프론트엔드에는 gameUrl이 필요할까?
    private String gameUrl;

    public static GameRoomResponse fromDto(GameRoomDto gameRoomDto) {
        return GameRoomResponse.builder()
                .id(gameRoomDto.getId())
                .title(gameRoomDto.getTitle())
                .maxPlayers(gameRoomDto.getMaxPlayers())
                .currentPlayers(gameRoomDto.getCurrentPlayers())
                .status(gameRoomDto.getStatus())
                .gameUrl(gameRoomDto.getGameUrl())
                .build();
    }
}
