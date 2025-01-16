package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.domain.dto.TierDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tiers")
@Data
public class TierJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "min_score", nullable = false)
    private int minScore;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public static TierDto toDto(TierJpaEntity tierJpaEntity){
        return TierDto.builder()
                .id(tierJpaEntity.getId())
                .name(tierJpaEntity.getName())
                .minScore(tierJpaEntity.minScore)
                .maxScore(tierJpaEntity.maxScore)
                .imageUrl(tierJpaEntity.imageUrl)
                .build();
    }
}
