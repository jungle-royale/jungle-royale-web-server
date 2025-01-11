package com.example.jungleroyal.service;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.dto.JwtTokenUserInfoDto;
import com.example.jungleroyal.infrastructure.BlackListJpaEntity;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.service.repository.BlackListRepository;
import com.example.jungleroyal.service.repository.RefreshRepository;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final BlackListRepository blackListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void saveJwtRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void updateJwtRefreshToken(RefreshToken refreshToken) {
        refreshToken.updateUpdatedAt(TimeUtils.createUtc());
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public void saveBlackList(String refreshToken) {
        BlackListJpaEntity blackListJpaEntity = BlackListJpaEntity.createBlackList(refreshToken);
        blackListRepository.save(blackListJpaEntity);
    }

    public boolean isBlacklisted(String refreshToken) {

        return blackListRepository.existsByInvalidRefreshToken(refreshToken);
    }

    public JwtTokenUserInfoDto extractUserInfo(String refreshToken) {
        String userId = jwtTokenProvider.extractSubject(refreshToken);
        String username = jwtTokenProvider.extractUsername(refreshToken);
        UserRole userRole = jwtTokenProvider.extractUserRole(refreshToken);

        return JwtTokenUserInfoDto.builder()
                .userId(userId)
                .username(username)
                .userRole(userRole)
                .build();
    }

    public String generateJwtToken(JwtTokenUserInfoDto jwtTokenUserInfoDto) {
        return jwtTokenProvider.generate(jwtTokenUserInfoDto.getUserId(), jwtTokenUserInfoDto.getUsername(), jwtTokenUserInfoDto.getUserRole());
    }

    public RefreshToken generateJwtRefreshToken(JwtTokenUserInfoDto jwtTokenUserInfoDto) {
        return jwtTokenProvider.generateRefreshToken(Long.parseLong(jwtTokenUserInfoDto.getUserId()), jwtTokenUserInfoDto.getUsername(),
                jwtTokenUserInfoDto.getUserRole());
    }
}
