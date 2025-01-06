package com.example.jungleroyal.domain.auth;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.domain.OAuthKakaoToken;
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
    private String refreshToken;
    private UserRole role = UserRole.MEMBER;

    public static KakaoLoginResponse createKakaoLoginResponse(String jwtToken, String jwtRefreshToken, String authRefreshToken){
        return KakaoLoginResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(jwtRefreshToken)
                .role(UserRole.MEMBER)
                .build();
    }
}
