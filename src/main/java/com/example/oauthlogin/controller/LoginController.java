package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.OAuthKakaoToken;
import com.example.oauthlogin.domain.dto.KakaoLoginResponse;
import com.example.oauthlogin.service.BlackListService;
import com.example.oauthlogin.service.KakaoAuthService;
import com.example.oauthlogin.service.UserService;
import com.example.oauthlogin.common.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login", description = "Login API")
@RequiredArgsConstructor
public class LoginController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlackListService blackListService;

    /**
     * 카카오 로그인 API
     *
     * @param payload
     * @return
     */
    @PostMapping("/kakao/login")
    @Operation(summary = "kakao Login", description = "카카오 로그인, 회원가입, jwt발행")
    public ResponseEntity<KakaoLoginResponse> kakaoCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code"); // 클라이언트에서 전송한 인가코드 추출

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Authorization code is missing");
        }
        KakaoLoginResponse kakaoLoginResponse = kakaoAuthService.loginWithKakao(code);

        return ResponseEntity.ok(kakaoLoginResponse);
    }

    @PostMapping("/verify-jwt")
    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestBody Map<String, String> payload) {
        String jwtToken = payload.get("jwt_token");

        System.out.println("여기가 jwt 검증구간입니다.");
        jwtTokenProvider.isValidToken(jwtToken);
        Map<String, Object> responseBody = new HashMap<>();

        return ResponseEntity.ok(responseBody);
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "JWT token is missing"));
//        }
//
//        try {
//            // JWT 검증 및 클레임 추출
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(jwtSecret) // SecretKey를 사용해 검증
//                    .build()
//                    .parseClaimsJws(jwtToken) // 토큰을 파싱하고 클레임 추출
//                    .getBody();
//
//            // 유효한 JWT인 경우 클레임 반환
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("subject", claims.getSubject());
//            responseBody.put("access_token", claims.get("access_token"));
//            responseBody.put("issued_at", claims.getIssuedAt());
//            responseBody.put("expiration", claims.getExpiration());
//
//            return ResponseEntity.ok(responseBody);
//
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            System.out.println("Access token has expired. Attempting to refresh...");
//
//            System.out.println("jwt 만료시 여기 호출!!!!");
//            // 2. 액세스 토큰 만료 시 리프레시 토큰 사용
//            if (refreshToken == null || refreshToken.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Access token expired and refresh token is missing"));
//            }
//
//            // 리프레시 토큰을 사용해 새로운 액세스 토큰 발급
//            try {
//                RestTemplate rt = new RestTemplate();
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//                params.add("grant_type", "refresh_token");
//                params.add("client_id", "clientId");
//                params.add("refresh_token", refreshToken);
//
//                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//                ResponseEntity<String> response = rt.exchange(
//                        "https://kauth.kakao.com/oauth/token",
//                        HttpMethod.POST,
//                        request,
//                        String.class
//                );
//
//                // 새 토큰 파싱
//                ObjectMapper objectMapper = new ObjectMapper();
//                OAuthToken newToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
//
//                // 새로운 액세스 토큰 반환
//                Map<String, Object> responseBody = new HashMap<>();
//                responseBody.put("access_token", newToken.getAccess_token());
//                responseBody.put("refresh_token", newToken.getRefresh_token());
//                responseBody.put("expires_in", newToken.getExpires_in());
//
//                return ResponseEntity.ok(responseBody);
//            } catch (HttpClientErrorException ex) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Failed to refresh token", "details", ex.getResponseBodyAsString()));
//            } catch (Exception ex) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred", "details", ex.getMessage()));
//            }
//        } catch (io.jsonwebtoken.SignatureException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid JWT signature"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JWT token"));
//        }
    }

    /**
     * RefreshToken 발급
     * @param refreshToken
     * @return access_token , refresh_token, expires_in
     */
    @PostMapping("/kakao/refresh-token")
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