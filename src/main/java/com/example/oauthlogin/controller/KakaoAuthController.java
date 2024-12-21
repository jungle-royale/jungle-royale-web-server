package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.OAuthToken;
import com.example.oauthlogin.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class KakaoAuthController {
//    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 랜덤한 SecretKey 생성
    private final SecretKey jwtSecret = Keys.hmacShaKeyFor("jungleroyaljungleroyalakbosolsol".getBytes());
//    private final String clientId = "9b5e1f47241e82beb559d44bd2a25377"; // 카카오 REST API 키 - 서버
    private final String clientId = "e8304b2a6b5aeb5020ef6abeb405115b"; // 카카오 REST API 키 - 프론트엔드
//    private final String redirectUri = "http://192.168.1.241:8080/api/auth/kakao/callback"; // 카카오 redirect_uri - 백엔드
    private final String redirectUri = "http://localhost:5173/login"; // 카카오 redirect_uri - 프론트엔드

    private final UserService userService;

    public KakaoAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code"); // 클라이언트에서 전송한 인가코드 추출
        System.out.println("code = " + code);

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authorization code is missing"));
        }

        // REST API 호출로 카카오 토큰 요청 처리 (생략된 기존 코드 추가)
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = null;
        try {
            response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
            System.out.println("정상 토큰 출력 : Response from Kakao: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("400 에러 발생 Error response from Kakao: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "500에러 발생 Error communicating with Kakao API"));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken;
        try {
            // 응답 본문 출력 (디버깅)
            System.out.println("Raw response body: " + response.getBody());

            // JSON 파싱
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패 시 원인 출력
            System.err.println("Failed to parse JSON: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse OAuth token", "details", e.getMessage()));
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error while parsing response"));
        }

        System.out.println("OAuthToken" + oAuthToken.getAccess_token());

        // HTTP POST를 요청할 때 보내는 데이터(body)를 설명해주는 헤더도 만들어 같이 보내줘야 한다.
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest= new HttpEntity<>(headers2);
//
//        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
//        ResponseEntity<String> response2 = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me", // https://{요청할 서버 주소}
//                HttpMethod.POST, // 요청할 방식
//                kakaoProfileRequest, // 요청할 때 보낼 데이터
//                String.class // 요청 시 반환되는 데이터 타입
//        );
//
//        System.out.println(":::::::: 카카오로부터 전달받은 개인정보 : " + response2.getBody());

        // JWT 생성
        String jwtToken = Jwts.builder()
                .setSubject("user") // 사용자 정보 (필요에 따라 사용자 ID 추가)
                .claim("access_token", oAuthToken.getAccess_token()) // 카카오 액세스 토큰 포함
                .setIssuedAt(Date.from(Instant.now())) // 발행 시간
                .setExpiration(Date.from(Instant.now().plusSeconds(3600))) // 만료 시간 (1시간)
                .signWith(jwtSecret) // SecretKey로 서명
                .compact();

        System.out.println("jwtToken = " + jwtToken);

        System.out.println("oAuthToken = " + oAuthToken.getRefresh_token());


        // 클라이언트로 JWT와 추가 정보 반환
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("jwt_token", jwtToken);
        responseBody.put("token_type", oAuthToken.getToken_type());
        responseBody.put("expires_in", String.valueOf(oAuthToken.getExpires_in()));
        responseBody.put("refresh_token", oAuthToken.getRefresh_token());

        System.out.println(":::::::::: 로그인 요청 시간 : " + LocalDateTime.now());

        // 회원번호 뽑아내기
        try {
            // 1. 카카오 공개 키 가져오기
            URL jwksURL = new URL("https://kauth.kakao.com/.well-known/jwks.json");
            JWKSet jwkSet = JWKSet.load(jwksURL);

            // 2. id_token 파싱
            JWSObject jwsObject = JWSObject.parse(oAuthToken.getId_token());
            RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(jwsObject.getHeader().getKeyID());

            // 3. 서명 검증
            JWSVerifier verifier = new RSASSAVerifier(rsaKey);
            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("Invalid ID token signature");
            }

            // 4. 페이로드 추출
            String payload2 = jwsObject.getPayload().toString();
            System.out.println("Decoded Payload: " + payload2);

            // 5. 회원 번호 추출
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload2, Map.class);
            String kakaoId = (String) claims.get("sub");
            String nickname = (String) claims.get("nickname");
            // 유저 이름이 없으면 기본값 설정
            if (nickname == null || nickname.isEmpty()) {
                nickname = "이름 없음";
            }
            System.out.println("회원 번호 (sub): " + kakaoId);
            System.out.println("nickname = " + nickname);

            if (kakaoId == null || kakaoId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid kakao_id"));
            }

            // 유저가 있으면 pass, 없으면 회원가입 시킴
            if (!userService.isUserExistsByKakaoId(kakaoId)){
                userService.saveOrUpdateUser(kakaoId, nickname, responseBody.get("refresh_token"),oAuthToken.getRefresh_token_expires_in());
            }

            // 클라이언트로 반환 데이터 추가
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse or verify id_token", "details", e.getMessage()));
        }

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/verify-jwt")
    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestBody Map<String, String> payload) {
        String jwtToken = payload.get("jwt_token");
        String refreshToken = payload.get("refresh_token");
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "JWT token is missing"));
        }

        try {
            // JWT 검증 및 클레임 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret) // SecretKey를 사용해 검증
                    .build()
                    .parseClaimsJws(jwtToken) // 토큰을 파싱하고 클레임 추출
                    .getBody();

            // 유효한 JWT인 경우 클레임 반환
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("subject", claims.getSubject());
            responseBody.put("access_token", claims.get("access_token"));
            responseBody.put("issued_at", claims.getIssuedAt());
            responseBody.put("expiration", claims.getExpiration());

            return ResponseEntity.ok(responseBody);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("Access token has expired. Attempting to refresh...");

            // 2. 액세스 토큰 만료 시 리프레시 토큰 사용
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Access token expired and refresh token is missing"));
            }

            // 리프레시 토큰을 사용해 새로운 액세스 토큰 발급
            try {
                RestTemplate rt = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("grant_type", "refresh_token");
                params.add("client_id", "clientId");
                params.add("refresh_token", refreshToken);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
                ResponseEntity<String> response = rt.exchange(
                        "https://kauth.kakao.com/oauth/token",
                        HttpMethod.POST,
                        request,
                        String.class
                );

                // 새 토큰 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                OAuthToken newToken = objectMapper.readValue(response.getBody(), OAuthToken.class);

                // 새로운 액세스 토큰 반환
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("access_token", newToken.getAccess_token());
                responseBody.put("refresh_token", newToken.getRefresh_token());
                responseBody.put("expires_in", newToken.getExpires_in());

                return ResponseEntity.ok(responseBody);
            } catch (HttpClientErrorException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Failed to refresh token", "details", ex.getResponseBodyAsString()));
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred", "details", ex.getMessage()));
            }
        } catch (io.jsonwebtoken.SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid JWT signature"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JWT token"));
        }
    }

    /**
     * RefreshToken 발급
     * @param payload
     * @return access_token , refresh_token, expires_in
     */
    @PostMapping("/kakao/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refresh_token"); // 클라이언트에서 전달된 리프레시 토큰
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing"));
        }

        RestTemplate rt = new RestTemplate();

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);

        // 요청 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            // 카카오 API에 POST 요청
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            // 응답 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            OAuthToken newToken = objectMapper.readValue(response.getBody(), OAuthToken.class);

            // 새로운 토큰 반환
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("access_token", newToken.getAccess_token());
            responseBody.put("refresh_token", newToken.getRefresh_token()); // 만약 리프레시 토큰이 갱신되었다면 반환
            responseBody.put("expires_in", String.valueOf(newToken.getExpires_in()));

            return ResponseEntity.ok(responseBody);
        } catch (HttpClientErrorException e) {
            // 카카오 API 요청 실패 처리
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", "Failed to refresh token", "details", e.getResponseBodyAsString()));
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred", "details", e.getMessage()));
        }
    }
}
