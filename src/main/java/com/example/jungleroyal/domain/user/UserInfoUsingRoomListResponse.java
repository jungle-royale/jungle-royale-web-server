package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoUsingRoomListResponse {
    private String username; // 사용자 이름
    private UserStatus userStatus;

    public static UserInfoUsingRoomListResponse create(String username, UserStatus userStatus){
        return UserInfoUsingRoomListResponse.builder()
                .userStatus(userStatus)
                .username(username)
                .build();
    }
}
