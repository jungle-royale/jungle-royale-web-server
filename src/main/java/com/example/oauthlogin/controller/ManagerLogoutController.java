package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.BlackList;
import com.example.oauthlogin.repository.BlackListRepository;
import com.example.oauthlogin.service.JwtService;
import com.example.oauthlogin.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ManagerLogoutController {
    private final KakaoAuthService kakaoAuthService;
    private final JwtService jwtService;
    private final BlackListRepository blackListRepository;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("authorization_access") String accessToken,
            @RequestHeader("authorization_refresh") String refreshToken
    ) {
        // Authorization 헤더에서 Bearer 제거
        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);

        String realAccessToken = accessToken.substring(7);
        String realRefreshToken = refreshToken.substring(7);

        System.out.println("realRefreshToken = " + realRefreshToken);
        System.out.println("realAccessToken = " + realAccessToken);

        // 2. JWT 토큰 무효화 처리
//        jwtService.invalidateToken(jwtToken);

        // 1. 카카오 로그아웃 요청
        kakaoAuthService.logoutFromKakao(realAccessToken);
        // 2. Refresh Token 블랙리스트 저장
        if (realRefreshToken != null && !realRefreshToken.isEmpty()) {
            blackListRepository.save(new BlackList(realRefreshToken));
        } else {
            throw new IllegalArgumentException("Invalid refresh token. Cannot save to blacklist.");
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("success", "true");
        return ResponseEntity.ok(responseBody);
    }
}
