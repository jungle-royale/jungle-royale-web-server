package com.example.jungleroyal.domain.auth;

import com.example.jungleroyal.common.types.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGuestLoginResponse {
    private String jwtToken;
    private String refreshToken;
    private UserRole role;

    public static UserGuestLoginResponse createUserGuestLoginResponse(String jwt, String refreshToken){
        return UserGuestLoginResponse.builder()
                .jwtToken(jwt)
                .refreshToken(refreshToken)
                .role(UserRole.GUEST)
                .build();
    }
}
