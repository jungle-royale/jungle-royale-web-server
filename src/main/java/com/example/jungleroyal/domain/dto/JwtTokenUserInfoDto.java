package com.example.jungleroyal.domain.dto;

import com.example.jungleroyal.common.types.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtTokenUserInfoDto {
    private String userId;
    private String username;
    private UserRole userRole;
}
