package com.example.jungleroyal.common.util;


import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthKakaoTokenUtils {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;


    @PostConstruct
    public void logKakaoConfig() {
        log.info("Kakao Configuration:");
        log.info("Client ID: {}", clientId);
        log.info("Redirect URI: {}", redirectUri);
    }

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;       // 1일
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일


    public OAuthKakaoToken generate(String accessCode){
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = null;
        try {
            response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("400에러 Error response from Kakao" + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error while communicating with Kakao API", e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthKakaoToken oAuthToken;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthKakaoToken.class);
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패 시 원인 출력
            e.printStackTrace();
            throw new RuntimeException("Failed to parse OAuth token", e);
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            throw new RuntimeException("Unexpected error while parsing response", e);
        }

        oAuthToken.setExpires_in((int) ACCESS_TOKEN_EXPIRE_TIME);
        return oAuthToken;
    }


    public OAuthKakaoToken generateRefreshToken(String refreshToken) {
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
            OAuthKakaoToken newToken = objectMapper.readValue(response.getBody(), OAuthKakaoToken.class);

            return newToken;
        } catch (HttpClientErrorException e) {
            // 카카오 API 요청 실패 처리
            throw new RuntimeException("Failed to refresh token: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }
    }

    public String logout(String accessToken){
        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // 카카오 액세스 토큰 추가

        // 요청 엔티티 생성
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            // 카카오 로그아웃 요청
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return "카카오 로그아웃 성공: " + response.getBody();
            } else {
                return "카카오 로그아웃 실패: " + response.getStatusCode();
            }

        } catch (Exception e) {
            throw new RuntimeException("카카오 로그아웃 요청 중 오류 발생", e);
        }
    }

    public String getKakaoIdUsingToken(OAuthKakaoToken oAuthKakaoToken) {
        String kakaoId;
        try {
            // 1. 카카오 공개 키 가져오기
            URL jwksURL = new URL("https://kauth.kakao.com/.well-known/jwks.json");
            JWKSet jwkSet = JWKSet.load(jwksURL);

            // 2. id_token 파싱
            JWSObject jwsObject = JWSObject.parse(oAuthKakaoToken.getId_token());
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
            kakaoId = (String) claims.get("sub");

            if (kakaoId == null || kakaoId.isEmpty()) {
                throw new RuntimeException("Invalid kakao_id");
            }

            // 클라이언트로 반환 데이터 추가
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 상세 메시지 반환
            return "Error: Failed to parse or verify id_token. Details: " + e.getMessage();
        }

        return kakaoId;
    }
}
