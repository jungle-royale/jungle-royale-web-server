package com.example.jungleroyal.domain.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoUsingRoomListResponse {
    private String username; // 사용자 이름

    public static UserInfoUsingRoomListResponse createUserInfoUsingRoomListResponse(String username){
        return UserInfoUsingRoomListResponse.builder()
                .username(username)
                .build();
    }
}
