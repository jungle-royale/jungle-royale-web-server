package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.OAuthKakaoToken;
import com.example.oauthlogin.util.AuthKakaoTokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final AuthKakaoTokenGenerator authKakaoTokenGenerator;
    public OAuthKakaoToken getKakaoTokenUsingAccessCode(String accessCode){
        // REST API 호출로 카카오 토큰 요청 처리 (생략된 기존 코드 추가)
        return authKakaoTokenGenerator.generate(accessCode);
    }

    public String getKakaoId(OAuthKakaoToken oAuthKakaoToken) {
        // 회원번호 뽑아내기
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

        System.out.println("kakaoId = " + kakaoId);

        return kakaoId;
    }
}
