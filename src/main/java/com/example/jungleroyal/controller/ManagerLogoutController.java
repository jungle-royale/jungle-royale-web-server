package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.BlackList;
import com.example.jungleroyal.domain.gameroom.GameRoomRequest;
import com.example.jungleroyal.domain.user.LogoutRequest;
import com.example.jungleroyal.repository.BlackListRepository;
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
@RequestMapping("/api/auth")
@Slf4j
public class ManagerLogoutController {
    private final KakaoAuthService kakaoAuthService;
    private final JwtService jwtService;
    private final BlackListRepository blackListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestBody LogoutRequest logoutRequest
    ) {
        log.info("비회원 로그인 실행");
//        System.out.println("jwtToken = " + jwtToken);
//        String userId = jwtTokenProvider.extractSubject(jwtToken);

//        return ResponseEntity.ok("전달완료!");
//        // Authorization 헤더에서 Bearer 제거
//        String realRefreshToken = refreshToken.substring(7);
//
//        // 2. JWT 토큰 무효화 처리
////        jwtService.invalidateToken(jwtToken);
//
//        // 1. 카카오 로그아웃 요청
//        kakaoAuthService.logoutFromKakao(realAccessToken);
//        // 2. Refresh Token 블랙리스트 저장
//        if (realRefreshToken != null && !realRefreshToken.isEmpty()) {
//            blackListRepository.save(new BlackList(realRefreshToken));
//        } else {
//            throw new IllegalArgumentException("Invalid refresh token. Cannot save to blacklist.");
//        }
//
        log.info("비회원 로그인 실행 완료");
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("success", "true");
        return ResponseEntity.ok(responseBody);
    }
}
