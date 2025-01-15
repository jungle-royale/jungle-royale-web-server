package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoUsingRoomListResponse {
    private String username; // 사용자 이름
    private UserStatus userStatus;
    private Integer score;
    private String rank;

    public static UserInfoUsingRoomListResponse create(String username, UserStatus userStatus, Integer score, String rank){
        return UserInfoUsingRoomListResponse.builder()
                .userStatus(userStatus)
                .username(username)
                .score(score)
                .rank(rank)
                .build();
    }
}
