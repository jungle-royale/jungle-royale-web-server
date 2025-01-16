package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.types.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String kakaoId;
    private String username;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private Integer gameMoney; // 게임머니 필드 추가
    private String currentGameUrl; // 현재 위치하고있는 게임룸 url
    private String clientId; // 방에 접속할 때 사용할 clientId
    private UserStatus userStatus; // 현재 유저 상태 : WAITING || IN_GAME
    private String giftImageUrl;
    private int totalKills; // 누적 킬 수
    private int totalFirstPlace; // 누적 1등 횟수

    public static UserDto fromUserEditMyPageRequest(Long userId, UserEditMyPageRequest userEditMyPageRequest){
        return builder()
                .id(userId)
                .username(userEditMyPageRequest.getUsername())
                .build();
    }
}
