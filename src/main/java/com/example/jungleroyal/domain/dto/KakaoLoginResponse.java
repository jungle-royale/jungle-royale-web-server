package com.example.jungleroyal.domain.dto;

import com.example.jungleroyal.common.types.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResponse {
    private String jwtToken;
    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private UserRole role;
}
