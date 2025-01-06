package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.domain.dto.AuthTokensDto;
import com.example.jungleroyal.infrastructure.AuthRefreshTokenJpaEntity;

public interface AuthRefreshTokenRepositoty {

    AuthRefreshTokenJpaEntity save(AuthRefreshTokenJpaEntity authRefreshTokenJpaEntity);

    AuthRefreshTokenJpaEntity getAuthTokensById(long userId);
}
