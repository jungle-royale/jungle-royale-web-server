package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.auth.JwtReissueResponse;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.service.JwtService;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    @PostMapping("/api/auth/refresh-token")
    public ResponseEntity<JwtReissueResponse> refreshToken(@RequestHeader("Authorization") String refresh) {
        String refreshToken = refresh.substring(7);
        // check if refresh is null
        if (refreshToken == null)
            return new ResponseEntity("refresh token null", HttpStatus.BAD_REQUEST);

        // check if refresh is valid
        if (!refreshTokenRepository.existsByRefreshToken(refreshToken))
            return new ResponseEntity("invalid refresh token", HttpStatus.BAD_REQUEST);

        String userId = jwtTokenProvider.extractSubject(refreshToken);
        String username = jwtTokenProvider.extractUsername(refreshToken);
        UserRole userRole = jwtTokenProvider.extractUserRole(refreshToken);

        // jwt 생성
        String jwt = jwtTokenProvider.generate(userId, username, userRole);
        // jwt 리프레시토큰 생성
        RefreshToken newRefreshToken = jwtTokenProvider.generateRefreshToken(Long.parseLong(userId), username, userRole);

        // rotate refresh
        jwtService.removeRefreshToken(refreshToken);
        jwtService.updateJwtRefreshToken(newRefreshToken);

        JwtReissueResponse response = JwtReissueResponse.createJwtReissueResponse(jwt, newRefreshToken.getRefreshToken());

        return ResponseEntity.ok(response);
    }
}
