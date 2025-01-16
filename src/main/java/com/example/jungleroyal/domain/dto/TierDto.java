package com.example.jungleroyal.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TierDto {
    private Long id;
    private String name;
    private int minScore;
    private int maxScore;
    private String imageUrl;
}
