package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.AuthRefreshTokenJpaEntity;

public interface AuthRefreshTokenRepositoty {

    AuthRefreshTokenJpaEntity save(AuthRefreshTokenJpaEntity authRefreshTokenJpaEntity);
}
