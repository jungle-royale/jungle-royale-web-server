package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 랜덤한 SecretKey 생성

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
//        params.add("client_id", "9b5e1f47241e82beb559d44bd2a25377");
        params.add("client_id", "e8304b2a6b5aeb5020ef6abeb405115b");
//        params.add("redirect_uri", "http://192.168.1.241:8080/api/auth/kakao/callback");
        params.add("redirect_uri", "http://localhost:5173/social-kakao");
//        params.add("redirect_uri", "http://192.168.1.136:5173");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

//        ResponseEntity<String> response;
//        response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );

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

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest= new HttpEntity<>(headers2);

        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
        ResponseEntity<String> response2 = rt.exchange(
                "https://kapi.kakao.com/v2/user/me", // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                kakaoProfileRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        System.out.println(":::::::: 카카오로부터 전달받은 개인정보 : " + response2.getBody());

        // JWT 생성
        String jwtToken = Jwts.builder()
                .setSubject("user") // 사용자 정보 (필요에 따라 사용자 ID 추가)
                .claim("access_token", oAuthToken.getAccess_token()) // 카카오 액세스 토큰 포함
                .setIssuedAt(Date.from(Instant.now())) // 발행 시간
                .setExpiration(Date.from(Instant.now().plusSeconds(3600))) // 만료 시간 (1시간)
                .signWith(jwtSecret) // SecretKey로 서명
                .compact();

        System.out.println("jwtToken = " + jwtToken);

        // 클라이언트로 JWT와 추가 정보 반환
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("jwt_token", jwtToken);
        responseBody.put("token_type", oAuthToken.getToken_type());
        responseBody.put("expires_in", String.valueOf(oAuthToken.getExpires_in()));

//        // 회원번호 뽑아내기
//        try {
//            // 1. 카카오 공개 키 가져오기
//            URL jwksURL = new URL("https://kauth.kakao.com/.well-known/jwks.json");
//            JWKSet jwkSet = JWKSet.load(jwksURL);
//
//            // 2. id_token 파싱
//            JWSObject jwsObject = JWSObject.parse(oAuthToken.getId_token());
//            RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(jwsObject.getHeader().getKeyID());
//
//            // 3. 서명 검증
//            JWSVerifier verifier = new RSASSAVerifier(rsaKey);
//            if (!jwsObject.verify(verifier)) {
//                throw new RuntimeException("Invalid ID token signature");
//            }
//
//            // 4. 페이로드 추출
//            String payload2 = jwsObject.getPayload().toString();
//            System.out.println("Decoded Payload: " + payload2);
//
//            // 5. 회원 번호 추출
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> claims = mapper.readValue(payload2, Map.class);
//            String userId = (String) claims.get("sub");
//            String nickname = (String) claims.get("nickname");
//            System.out.println("회원 번호 (sub): " + userId);
//            System.out.println("nickname = " + nickname);
//            // 클라이언트로 반환 데이터 추가
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Failed to parse or verify id_token", "details", e.getMessage()));
//        }

        return ResponseEntity.ok(responseBody);
    }
}
