package com.example.jungleroyal.domain.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String kakaoId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    public static UserDto fromUserEditMyPageRequest(Long userId, UserEditMyPageRequest userEditMyPageRequest){
        return builder()
                .id(userId)
                .username(userEditMyPageRequest.getUsername())
                .build();
    }
}
