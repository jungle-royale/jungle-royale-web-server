package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlackListRepositoryImpl implements BlackListRepository {

    private final BlackListJpaRepository blackListJpaRepository;

    @Override
    public boolean existsByInvalidRefreshToken(String refreshToken) {
        // 입력값 검증
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token must not be null or empty.");
        }

        // JpaRepository 호출
        return blackListJpaRepository.existsByInvalidRefreshToken(refreshToken);
    }

    @Override
    public void save(BlackListJpaEntity blackListJpaEntity) {

    }
}
