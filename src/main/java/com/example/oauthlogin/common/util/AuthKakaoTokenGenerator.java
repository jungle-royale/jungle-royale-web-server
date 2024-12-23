package com.example.oauthlogin.common.util;


import com.example.oauthlogin.domain.OAuthKakaoToken;
import com.example.oauthlogin.repository.BlackListRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthKakaoTokenGenerator {
    private static final String BEARER_TYPE = "Bearer";

//        private final String clientId = "9b5e1f47241e82beb559d44bd2a25377"; // 카카오 REST API 키 - 서버
    private final String clientId = "e8304b2a6b5aeb5020ef6abeb405115b"; // 카카오 REST API 키 - 프론트엔드
//        private final String redirectUri = "http://192.168.1.241:8080/api/auth/kakao/callback"; // 카카오 redirect_uri - 백엔드
    private final String redirectUri = "http://localhost:5173/login"; // 카카오 redirect_uri - 프론트엔드
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 5;       // 5분
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
            // 응답 본문 출력 (디버깅)
            System.out.println("Raw response body: " + response.getBody());

            // JSON 파싱
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
        System.out.println("여기 실행11111111?");
        try {
            // 카카오 API에 POST 요청
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            System.out.println("여기 실행?");

            // 응답 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            OAuthKakaoToken newToken = objectMapper.readValue(response.getBody(), OAuthKakaoToken.class);

            return newToken;
        } catch (HttpClientErrorException e) {
            // 카카오 API 요청 실패 처리
            System.err.println("HTTP Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to refresh token: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("Unexpected Error: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }
    }


}
