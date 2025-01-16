package com.example.jungleroyal.domain.dto;

import com.example.jungleroyal.infrastructure.UserJpaEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingResponseDTO {
    private String username;
    private String tier;
    private Integer score;

    public static RankingResponseDTO from(UserJpaEntity user, String tier) {
        return RankingResponseDTO.builder()
                .username(user.getUsername())
                .tier(tier)
                .score(user.getScore())
                .build();
    }
}
