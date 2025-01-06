package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.exceptions.BlacklistTokenException;
import com.example.jungleroyal.common.exceptions.TokenExpiredException;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.auth.JwtReissueResponse;
import com.example.jungleroyal.domain.dto.JwtTokenUserInfoDto;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.service.JwtService;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    @PostMapping("/api/auth/refresh")
    public ResponseEntity<JwtReissueResponse> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String refresh) {

        if (refresh == null || !refresh.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 없거나 잘못된 형식입니다.");
        }

        String refreshToken = refresh.substring(7);

        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is null");
        }

        if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        if (jwtService.isBlacklisted(refreshToken)) {
            throw new BlacklistTokenException("Refresh token is blacklisted");
        }

        if (jwtTokenProvider.isRefreshTokenExpired(refreshToken)) {
            throw new TokenExpiredException("Refresh token has expired. Please log in again.");
        }

        JwtTokenUserInfoDto jwtTokenUserInfoDto = jwtService.extractUserInfo(refreshToken);

        jwtService.generateJwtToken(jwtTokenUserInfoDto);
        // jwt 생성
        String jwtToken = jwtService.generateJwtToken(jwtTokenUserInfoDto);
        RefreshToken newRefreshToken = jwtService.generateJwtRefreshToken(jwtTokenUserInfoDto);
        // jwt 리프레시토큰 생성

        // rotate refresh
        jwtService.removeRefreshToken(refreshToken);
        jwtService.updateJwtRefreshToken(newRefreshToken);

        JwtReissueResponse response = JwtReissueResponse.createJwtReissueResponse(jwtToken, newRefreshToken.getRefreshToken());

        return ResponseEntity.ok(response);
    }
}
