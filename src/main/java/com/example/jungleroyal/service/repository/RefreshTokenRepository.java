package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.infrastructure.UserJpaEntity;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    void delete(RefreshToken refreshToken);

    void deleteByRefreshToken(String refreshToken);

    // refreshToken으로 RefreshToken 엔티티 조회
    boolean existsByRefreshToken(String refresh);
}
