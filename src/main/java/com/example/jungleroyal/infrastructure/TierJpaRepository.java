package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TierJpaRepository extends JpaRepository<TierJpaEntity, Long> {
    Optional<TierJpaEntity> findByMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(int score, int score2);
}
