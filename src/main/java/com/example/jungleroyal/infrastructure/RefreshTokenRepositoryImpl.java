package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.domain.RefreshToken;
import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }
}
