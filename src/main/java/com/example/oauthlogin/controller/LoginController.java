package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.OAuthKakaoToken;
import com.example.oauthlogin.service.KakaoAuthService;
import com.example.oauthlogin.service.UserService;
import com.example.oauthlogin.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/kakao/login")
    public ResponseEntity<Map<String, String>> kakaoCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code"); // 클라이언트에서 전송한 인가코드 추출
        System.out.println("code = " + code);

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authorization code is missing"));
        }
        OAuthKakaoToken oAuthKakaoToken = kakaoAuthService.getKakaoTokenUsingAccessCode(code);

        String jwtToken = jwtTokenProvider.generateKakaoJwt("user", oAuthKakaoToken);




        String kakaoId = kakaoAuthService.getKakaoId(oAuthKakaoToken);

        // 유저가 이미 존재하는 경우 리프레시 토큰만 업데이트
        if (!userService.isUserExistsByKakaoId(kakaoId)) {
            userService.updateRefreshTokenByKakaoId(kakaoId, oAuthKakaoToken.getRefresh_token(), oAuthKakaoToken.getRefresh_token_expires_in());
        }

        // 클라이언트로 JWT와 추가 정보 반환
        Map<String, String> responseBody = new HashMap<>();

        responseBody.put("jwt_token", jwtToken);
        responseBody.put("token_type", oAuthKakaoToken.getToken_type());
        responseBody.put("expires_in", String.valueOf(oAuthKakaoToken.getExpires_in()));
        responseBody.put("refresh_token", oAuthKakaoToken.getRefresh_token());
        responseBody.put("is_guest", "false");
        return ResponseEntity.ok(responseBody);
    }
}
//    @PostMapping("/verify-jwt")
//    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestBody Map<String, String> payload) {
//        String jwtToken = payload.get("jwt_token");
//        String refreshToken = payload.get("refresh_token");
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
//    }
//
//    /**
//     * RefreshToken 발급
//     * @param payload
//     * @return access_token , refresh_token, expires_in
//     */
//    @PostMapping("/kakao/refresh-token")
//    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> payload) {
//        String refreshToken = payload.get("refresh_token"); // 클라이언트에서 전달된 리프레시 토큰
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing"));
//        }
//
//        RestTemplate rt = new RestTemplate();
//
//        // HTTP 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // 요청 파라미터 설정
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "refresh_token");
//        params.add("client_id", clientId);
//        params.add("refresh_token", refreshToken);
//
//        // 요청 생성
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        try {
//            // 카카오 API에 POST 요청
//            ResponseEntity<String> response = rt.exchange(
//                    "https://kauth.kakao.com/oauth/token",
//                    HttpMethod.POST,
//                    kakaoTokenRequest,
//                    String.class
//            );
//
//            // 응답 JSON 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            OAuthToken newToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
//
//            // 새로운 토큰 반환
//            Map<String, String> responseBody = new HashMap<>();
//            responseBody.put("access_token", newToken.getAccess_token());
//            responseBody.put("refresh_token", newToken.getRefresh_token()); // 만약 리프레시 토큰이 갱신되었다면 반환
//            responseBody.put("expires_in", String.valueOf(newToken.getExpires_in()));
//
//            return ResponseEntity.ok(responseBody);
//        } catch (HttpClientErrorException e) {
//            // 카카오 API 요청 실패 처리
//            return ResponseEntity.status(e.getStatusCode())
//                    .body(Map.of("error", "Failed to refresh token", "details", e.getResponseBodyAsString()));
//        } catch (Exception e) {
//            // 기타 예외 처리
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Unexpected error occurred", "details", e.getMessage()));
//        }
