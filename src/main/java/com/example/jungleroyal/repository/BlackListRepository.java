package com.example.jungleroyal.repository;

public interface BlackListRepository {
    boolean existsByInvalidRefreshToken(String refreshToken);

    void save(BlackListJpaEntity blackListJpaEntity);
}
