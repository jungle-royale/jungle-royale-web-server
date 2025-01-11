package com.example.jungleroyal.service;

import com.example.jungleroyal.common.types.AuthType;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.AuthKakaoTokenUtils;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.OAuthKakaoToken;
import com.example.jungleroyal.domain.auth.KakaoLoginResponse;
import com.example.jungleroyal.domain.dto.AuthTokensDto;
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
    private final AuthKakaoTokenUtils authKakaoTokenUtils;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final AuthRefreshTokenRepositoty authRefreshTokenRepositoty;

    public OAuthKakaoToken getKakaoTokenUsingAccessCode(String accessCode){
        // REST API 호출로 카카오 토큰 요청 처리 (생략된 기존 코드 추가)
        return authKakaoTokenUtils.generate(accessCode);
    }

    public String getKakaoIdUsingToken(OAuthKakaoToken oAuthKakaoToken) {
        String kakaoId = authKakaoTokenUtils.getKakaoIdUsingToken(oAuthKakaoToken);
        // 회원번호 뽑아내기
        return kakaoId;
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
                .accessToken(oAuthKakaoToken.getAccess_token())
                .refreshToken(oAuthKakaoToken.getRefresh_token())
                .expiredAt(expiredAt)
                .updatedAt(TimeUtils.createUtc())
                .createdAt(TimeUtils.createUtc())
                .build();
        return authRefreshTokenRepositoty.save(authRefreshTokenJpaEntity);
    }

}
