package com.example.jungleroyal.common.util;


import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.example.jungleroyal.repository.BlackListRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthKakaoTokenGenerator {

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

    private static final String BEARER_TYPE = "Bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;       // 1시간
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


}
