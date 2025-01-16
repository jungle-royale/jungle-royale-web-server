package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TierRepositoryImpl implements TierRepository {
    private final TierJpaRepository tierJpaRepository;

    @Override
    public Optional<TierJpaEntity> findByScore(int score1, int score2) {
        return tierJpaRepository.findByMinScoreLessThanEqualAndMaxScoreGreaterThanEqual(score1, score2);
    }
}
