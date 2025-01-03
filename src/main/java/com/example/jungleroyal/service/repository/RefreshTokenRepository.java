package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.domain.RefreshToken;

public interface RefreshTokenRepository {

    void save(RefreshToken refreshToken);
}
