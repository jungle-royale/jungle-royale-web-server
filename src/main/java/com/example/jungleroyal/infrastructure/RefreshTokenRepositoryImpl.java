package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenJpaRepository.findByUserId(userId);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenJpaRepository.delete(refreshToken);
    }



    @Override
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public boolean existsByRefreshToken(String refresh) {
        return refreshTokenJpaRepository.findByRefreshToken(refresh).isPresent();
    }
}
