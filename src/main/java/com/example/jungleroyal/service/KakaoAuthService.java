package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.domain.dto.KakaoLoginResponse;
import com.example.jungleroyal.common.util.AuthKakaoTokenGenerator;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final AuthKakaoTokenGenerator authKakaoTokenGenerator;

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginResponse getKakaoLoginResponse(String jwtToken, OAuthKakaoToken oAuthKakaoToken){
        KakaoLoginResponse response = KakaoLoginResponse.builder()
                .jwtToken(jwtToken)
                .accessToken(oAuthKakaoToken.getAccess_token())
                .refreshToken(oAuthKakaoToken.getRefresh_token())
                .expiresIn(String.valueOf(oAuthKakaoToken.getExpires_in()))
                .role(UserRole.MEMBER)
                .build();
        return response;
    }

    public OAuthKakaoToken getKakaoTokenUsingAccessCode(String accessCode){
        // REST API 호출로 카카오 토큰 요청 처리 (생략된 기존 코드 추가)
        return authKakaoTokenGenerator.generate(accessCode);
    }

    public String getKakaoIdUsingToken(OAuthKakaoToken oAuthKakaoToken) {
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



    public void logoutFromKakao(String accessToken) {
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    kakaoLogoutUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to logout from Kakao: " + e.getMessage(), e);
        }
    }

    public OAuthKakaoToken getKakaoRefreshToken(String refreshToken) {
        return authKakaoTokenGenerator.generateRefreshToken(refreshToken);
    }

    // TODO: login 관련 추상화 필요
    public KakaoLoginResponse loginWithKakao(String code) {
        OAuthKakaoToken oAuthKakaoToken = getKakaoTokenUsingAccessCode(code);

        String kakaoId = getKakaoIdUsingToken(oAuthKakaoToken);

        userService.kakaoUserJoin(kakaoId, oAuthKakaoToken);
        // 카카오회원 번호를 이용해서 jwt 생성
        UserDto userByKakaoId = userService.getUserByKakaoId(kakaoId);
        long userId = userByKakaoId.getId();
        String username = userByKakaoId.getUsername();
        String userRole = userByKakaoId.getUserRole().name();
        String kakaoId2 = userByKakaoId.getKakaoId();

        String jwtToken = jwtTokenProvider.generateKakaoJwt(String.valueOf(userId), username, userRole, kakaoId2);

        return getKakaoLoginResponse(jwtToken, oAuthKakaoToken);
    }
}
