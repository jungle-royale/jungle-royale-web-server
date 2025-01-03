package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.BlackListJpaEntity;

public interface BlackListRepository {
    boolean existsByInvalidRefreshToken(String refreshToken);

    void save(BlackListJpaEntity blackListJpaEntity);
}
