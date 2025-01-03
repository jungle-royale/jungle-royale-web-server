package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.domain.RefreshToken;

public interface RefreshTokenRepository {

    void save(RefreshToken refreshToken);
}
