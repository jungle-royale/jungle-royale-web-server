package com.example.oauthlogin.domain.user;

import com.example.oauthlogin.common.types.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGuestLoginResponse {
    private String jwtToken;
    private UserRole role;

    public static UserGuestLoginResponse createUserGuestLoginResponse(String jwt){
        return UserGuestLoginResponse.builder()
                .jwtToken(jwt)
                .role(UserRole.GUEST)
                .build();
    }
}
