package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.domain.auth.LogoutRequest;
import com.example.jungleroyal.service.repository.BlackListRepository;
import com.example.jungleroyal.service.JwtService;
import com.example.jungleroyal.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ManagerLogoutController {
    private final JwtService jwtService;
    private final SecurityUtil securityUtil;

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestBody LogoutRequest logoutRequest
    ) {
        String refreshToken = logoutRequest.getJwtRefreshToken();

        jwtService.saveBlackList(refreshToken);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("success", "true");
        return ResponseEntity.ok(responseBody);

    }

}
