package com.example.oauthlogin.dto;

import com.example.oauthlogin.service.KakaoAuthService;
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
    private boolean isGuest;
}
