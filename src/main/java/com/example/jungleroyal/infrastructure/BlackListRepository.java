package com.example.jungleroyal.infrastructure;

public interface BlackListRepository {
    boolean existsByInvalidRefreshToken(String refreshToken);

    void save(BlackListJpaEntity blackListJpaEntity);
}
