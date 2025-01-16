package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.dto.TierDto;
import com.example.jungleroyal.infrastructure.TierJpaEntity;
import com.example.jungleroyal.service.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TierService {

    private final TierRepository tierRepository;

    public TierDto getTierByScore(int score) {
        TierJpaEntity tierJpaEntity = tierRepository.findByScore(score, score)
                .orElseThrow(() -> new IllegalArgumentException("해당 점수에 해당하는 티어를 찾을 수 없습니다."));

        return TierJpaEntity.toDto(tierJpaEntity);
    }
}
