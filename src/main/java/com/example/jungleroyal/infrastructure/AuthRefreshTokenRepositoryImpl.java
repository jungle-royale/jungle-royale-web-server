package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.AuthRefreshTokenRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRefreshTokenRepositoryImpl implements AuthRefreshTokenRepositoty {
    private final AuthRefreshTokenJpaRepository authRefreshTokenJpaRepository;

    @Override
    public AuthRefreshTokenJpaEntity save(AuthRefreshTokenJpaEntity authRefreshTokenJpaEntity) {
        return authRefreshTokenJpaRepository.save(authRefreshTokenJpaEntity);
    }
}
