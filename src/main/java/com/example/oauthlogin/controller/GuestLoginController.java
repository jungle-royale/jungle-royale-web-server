package com.example.oauthlogin.controller;

import com.example.oauthlogin.common.types.UserRole;
import com.example.oauthlogin.common.util.JwtTokenProvider;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.domain.user.UserGuestLoginResponse;
import com.example.oauthlogin.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/guest")
@Tag(name = "GuestLogin", description = "GuestLogin API")
@RequiredArgsConstructor
public class GuestLoginController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<UserGuestLoginResponse> guestLogin() {
        // 1. 비회원 유저 생성
        User guestUser = userService.registerGuest();

        // 2. JWT 생성
        String jwt = jwtTokenProvider.generate(String.valueOf(guestUser.getId()));

        // 3. 응답 데이터 구성
        UserGuestLoginResponse response = UserGuestLoginResponse.createUserGuestLoginResponse(jwt);

        return ResponseEntity.ok(response);
    }
}
