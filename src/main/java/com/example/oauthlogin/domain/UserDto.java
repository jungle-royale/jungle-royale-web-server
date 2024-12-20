package com.example.oauthlogin.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private LocalDateTime createdAt = LocalDateTime.now();

}
