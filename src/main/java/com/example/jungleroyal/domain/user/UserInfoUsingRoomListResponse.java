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
    private String tier;
//    private String tierImageUrl; // 티어 이미지 URL 추가


    public static UserInfoUsingRoomListResponse create(String username, UserStatus userStatus, Integer score, String rank, String tier){
        return UserInfoUsingRoomListResponse.builder()
                .userStatus(userStatus)
                .username(username)
                .score(score)
                .rank(rank)
                .tier(tier)
//                .tierImageUrl(tierImageUrl)
                .build();
    }
}
