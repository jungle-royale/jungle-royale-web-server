package com.example.jungleroyal.service;

import com.example.jungleroyal.common.types.AuthType;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.AuthKakaoTokenGenerator;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.example.jungleroyal.domain.auth.KakaoLoginResponse;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.AuthRefreshTokenJpaEntity;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.service.repository.AuthRefreshTokenRepositoty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final AuthKakaoTokenGenerator authKakaoTokenGenerator;

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final AuthRefreshTokenRepositoty authRefreshTokenRepositoty;

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


    // TODO: login 관련 추상화 필요
    public KakaoLoginResponse loginWithKakao(String code) {
        // 카카오 토큰 받기
        OAuthKakaoToken oAuthKakaoToken = getKakaoTokenUsingAccessCode(code);

        String kakaoId = getKakaoIdUsingToken(oAuthKakaoToken);

        // 유저 정보가 없으면 회원가입
        if(!userService.isUserExistsByKakaoId(kakaoId)){
            userService.kakaoUserJoin(kakaoId);
        }

        UserDto user = userService.getUserByKakaoId(kakaoId);
        long userId = user.getId();
        String username = user.getUsername();
        UserRole userRole = user.getUserRole();

        // jwt 생성
        String jwtToken = jwtTokenProvider.generateKakaoJwt(String.valueOf(userId), username, userRole.name(), kakaoId);

        // jwt 리프레시 토큰 생성
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), username, userRole);
        jwtService.saveJwtRefreshToken(refreshToken);

        // 카카오 리프레시 토큰 생성 및 저장
        AuthRefreshTokenJpaEntity authRefreshTokenJpaEntity = saveAuthRefeshToken(oAuthKakaoToken, user.getId(), AuthType.KAKAO);

        return KakaoLoginResponse.createKakaoLoginResponse(jwtToken, refreshToken.getRefreshToken(), authRefreshTokenJpaEntity.getRefreshToken());
    }

    private AuthRefreshTokenJpaEntity saveAuthRefeshToken(OAuthKakaoToken oAuthKakaoToken, Long userId, AuthType authType) {
        LocalDateTime expiredAt = TimeUtils.setExpiredAt(oAuthKakaoToken.getRefresh_token_expires_in());
        AuthRefreshTokenJpaEntity authRefreshTokenJpaEntity = AuthRefreshTokenJpaEntity.builder()
                .userId(userId)
                .authType(authType)
                .refreshToken(oAuthKakaoToken.getRefresh_token())
                .expiredAt(expiredAt)
                .updatedAt(TimeUtils.createUtc())
                .createdAt(TimeUtils.createUtc())
                .build();
        return authRefreshTokenRepositoty.save(authRefreshTokenJpaEntity);
    }
}
