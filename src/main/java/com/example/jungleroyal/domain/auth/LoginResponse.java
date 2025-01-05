package com.example.jungleroyal.domain.auth;

import com.example.jungleroyal.common.types.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String jwtToken;
    private String refreshToken;
    private UserRole role;
}
