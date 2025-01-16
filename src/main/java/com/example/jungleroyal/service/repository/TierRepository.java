package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.TierJpaEntity;

import java.util.Optional;

public interface TierRepository {
    Optional<TierJpaEntity> findByScore(int score1, int score2);
}
