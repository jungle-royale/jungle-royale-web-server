package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.example.jungleroyal.domain.dto.KakaoLoginResponse;
import com.example.jungleroyal.service.BlackListService;
import com.example.jungleroyal.service.KakaoAuthService;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
@Tag(name = "kakaoLogin", description = "kakaoLogin API")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlackListService blackListService;

    /**
     * 카카오 로그인 API
     *
     * @param payload
     * @return
     */
    @PostMapping("/api/auth/kakao/login")
    @Operation(summary = "kakao Login", description = "카카오 로그인, 회원가입, jwt발행")
    public ResponseEntity<KakaoLoginResponse> kakaoCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code"); // 클라이언트에서 전송한 인가코드 추출

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

    /**
     * RefreshToken 발급
     * @param refreshToken
     * @return access_token , refresh_token, expires_in
     */
    @PostMapping("/api/auth/kakao/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestHeader("authorization_refresh") String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing"));
        }

        // 블랙리스트에 있는 토큰인지 확인하고 예외 발생
        if (blackListService.isBlackListToken(refreshToken)) {
            throw new IllegalArgumentException("[ERROR] 해당 Refresh Token은 블랙리스트에 등록되어 사용이 불가능합니다.");
        }

        String realRefreshToken = refreshToken.substring(7);
        // 리프레시 토큰 발급
        OAuthKakaoToken oAuthKakaoToken = kakaoAuthService.getKakaoRefreshToken(realRefreshToken);

        // Todo : 리프레시 토큰 재발급을 위한 객체 생성 후 이용하기
        Map<String, String> responseBody = new HashMap<>();

        responseBody.put("expires_in", String.valueOf(oAuthKakaoToken.getExpires_in()));
        responseBody.put("refresh_token", oAuthKakaoToken.getRefresh_token());
        responseBody.put("access_token", oAuthKakaoToken.getAccess_token());
        responseBody.put("is_guest", "false");
        responseBody.put("success", "true");

        return ResponseEntity.ok(responseBody);
    }
}