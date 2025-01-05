package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.domain.auth.UserGuestLoginResponse;
import com.example.jungleroyal.service.JwtService;
import com.example.jungleroyal.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "GuestLogin", description = "GuestLogin API")
@RequiredArgsConstructor
@Slf4j
public class GuestLoginController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;
    private final JwtService jwtService;

    @PostMapping("/api/auth/guest/login")
    public ResponseEntity<UserGuestLoginResponse> guestLogin() {

        // 1. 비회원 유저 생성
        UserJpaEntity guestUserJpaEntity = userService.registerGuest();
        Long id = guestUserJpaEntity.getId();
        String username = guestUserJpaEntity.getUsername();
        UserRole userRole = guestUserJpaEntity.getRole();
        String userId = String.valueOf(id);

        // 2. JWT 생성
        String jwt = jwtTokenProvider.generate(userId, username, userRole);
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(guestUserJpaEntity.getId(), username, userRole);

        // 3. 응답 데이터 구성
        UserGuestLoginResponse response = UserGuestLoginResponse.createUserGuestLoginResponse(jwt, refreshToken.getRefreshToken());

        jwtService.saveJwtRefreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }
}
