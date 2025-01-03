package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.domain.user.UserGuestLoginResponse;
import com.example.jungleroyal.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/guest")
@Tag(name = "GuestLogin", description = "GuestLogin API")
@RequiredArgsConstructor
@Slf4j
public class GuestLoginController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/rooms/login")
    public ResponseEntity<UserGuestLoginResponse> guestLogin() {
        // 1. 비회원 유저 생성
        UserJpaEntity guestUserJpaEntity = userService.registerGuest();
        Long id = guestUserJpaEntity.getId();
        String userId = String.valueOf(id);
        // 2. JWT 생성
        String jwt = jwtTokenProvider.generate(userId);

        // 3. 응답 데이터 구성
        UserGuestLoginResponse response = UserGuestLoginResponse.createUserGuestLoginResponse(jwt);
        log.info("response생성");

        return ResponseEntity.ok(response);
    }
}
