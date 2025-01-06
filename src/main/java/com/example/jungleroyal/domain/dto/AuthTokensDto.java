package com.example.jungleroyal.domain.dto;

import com.example.jungleroyal.common.types.AuthType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthTokensDto {
    private String accessToken;
    private String refreshToken;
    private AuthType authType;
}
