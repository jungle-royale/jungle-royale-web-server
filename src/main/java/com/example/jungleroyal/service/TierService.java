package com.example.jungleroyal.service;

import com.example.jungleroyal.common.config.TierConfig;
import com.example.jungleroyal.domain.dto.TierDto;
import com.example.jungleroyal.infrastructure.TierJpaEntity;
import com.example.jungleroyal.service.repository.TierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TierService {
    private final TierConfig tierConfig;

//    public TierConfig.Tier getTierByScore(int score) {
//
//        if (tierConfig.getTiers() == null || tierConfig.getTiers().isEmpty()) {
//            throw new IllegalStateException("Tier configuration is not loaded properly.");
//        }
//
//        // 점수에 해당하는 티어 조회
//        return tierConfig.getTiers().stream()
//                .filter(tier -> score >= tier.getMinScore() && score <= tier.getMaxScore())
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Invalid score for tier"));
//    }

    private final TierRepository tierRepository;

    public TierDto getTierByScore(int score) {
        TierJpaEntity tierJpaEntity = tierRepository.findByScore(score, score)
                .orElseThrow(() -> new IllegalArgumentException("해당 점수에 해당하는 티어를 찾을 수 없습니다."));

        return TierJpaEntity.toDto(tierJpaEntity);
    }
}
