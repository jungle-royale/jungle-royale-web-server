package com.example.jungleroyal.domain.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogoutRequest {
    private String jwtRefreshToken;
}
