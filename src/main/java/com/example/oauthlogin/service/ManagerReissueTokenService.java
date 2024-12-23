package com.example.oauthlogin.service;

import com.example.oauthlogin.auth.AuthTokensResponse;
import com.example.oauthlogin.common.util.AuthTokensGenerator;
import com.example.oauthlogin.common.util.RefreshTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ManagerReissueTokenService {
    private final AuthTokensGenerator authTokensGenerator;
    private final RefreshTokenValidator refreshTokenValidator;

    public AuthTokensResponse reissueToken(final String refreshToken) {
        refreshTokenValidator.validateToken(refreshToken);
        refreshTokenValidator.validateLogoutToken(refreshToken);
        final Long memberId = authTokensGenerator.extractMemberId(refreshToken);
        return authTokensGenerator.generate(memberId);
    }
}
