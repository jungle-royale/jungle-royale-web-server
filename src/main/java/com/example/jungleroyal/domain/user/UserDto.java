package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserRole;
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

    public static UserDto fromUserEditMyPageRequest(Long userId, UserEditMyPageRequest userEditMyPageRequest){
        return builder()
                .id(userId)
                .username(userEditMyPageRequest.getUsername())
                .build();
    }
}
