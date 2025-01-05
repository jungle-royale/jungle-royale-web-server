package com.example.jungleroyal.domain.auth;

import com.example.jungleroyal.common.types.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtReissueResponse {
    private String jwtToken;
    private String refreshToken;
    private UserRole role;

    public static JwtReissueResponse createJwtReissueResponse(String jwtToken, String refreshToken){
        return JwtReissueResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .role(UserRole.MEMBER)
                .build();
    }

}
