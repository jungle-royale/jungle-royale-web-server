package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.auth.KakaoLoginResponse;
import com.example.jungleroyal.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
@Tag(name = "kakaoLogin", description = "kakaoLogin API")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인 API
     *
     * @param payload
     * @return
     */
    @PostMapping("/api/auth/kakao/login")
    @Operation(summary = "kakao Login", description = "카카오 로그인, 회원가입, jwt발행")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestBody Map<String, String> payload) {
        String code = payload.get("code"); // 클라이언트에서 전송한 인가코드
        // TODO: 에러메시지 클라이언트에게 전송하기
        KakaoLoginResponse kakaoLoginResponse = kakaoAuthService.loginWithKakao(code);

        return ResponseEntity.ok(kakaoLoginResponse);
    }

    @PostMapping("/api/auth/kakao/verify-jwt")
    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestBody Map<String, String> payload) {
        String jwtToken = payload.get("jwt_token");

        System.out.println("여기가 jwt 검증구간입니다.");
        jwtTokenProvider.isValidToken(jwtToken);
        Map<String, Object> responseBody = new HashMap<>();

        return ResponseEntity.ok(responseBody);
    }
}