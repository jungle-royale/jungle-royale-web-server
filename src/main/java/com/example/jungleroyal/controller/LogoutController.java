package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.domain.auth.LogoutRequest;
import com.example.jungleroyal.domain.dto.AuthTokensDto;
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
public class LogoutController {
    private final JwtService jwtService;
    private final SecurityUtil securityUtil;
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestBody LogoutRequest logoutRequest
    ) {
        String userRole = securityUtil.getUserRole();
        String refreshToken = logoutRequest.getRefreshToken();

        jwtService.saveBlackList(refreshToken);

        String userId = securityUtil.getUserId();

        if(userRole == UserRole.MEMBER.name()){
            AuthTokensDto authTokens = kakaoAuthService.getAuthTokens(userId);

            // 카카오 서버 로그아웃 요청
            kakaoAuthService.logoutFromKakao(authTokens.getAccessToken());
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("success", "true");
        return ResponseEntity.ok(responseBody);

    }

}
