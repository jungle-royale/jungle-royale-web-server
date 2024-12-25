package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.domain.user.UserInfoUsingRoomListResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameRoomListWithUserReponse {
    private UserInfoUsingRoomListResponse userInfo; // 사용자 정보
    private List<GameRoomListResponse> gameRooms; // 게임 방 리스트


    public static GameRoomListWithUserReponse createGameRoomListWithUserReponse(
            UserInfoUsingRoomListResponse userInfo,
            List<GameRoomListResponse> gameRooms
    ){
        return GameRoomListWithUserReponse.builder()
                .userInfo(userInfo)
                .gameRooms(gameRooms)
                .build();
    }
}
