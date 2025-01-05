package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RefreshToken createAndSaveRefreshToken(UserJpaEntity user) {
        // 기존 토큰 제거
        refreshTokenRepository.findByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return refreshTokenRepository.save(refreshToken);
    }
}
