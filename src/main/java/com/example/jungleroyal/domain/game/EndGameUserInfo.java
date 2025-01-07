package com.example.jungleroyal.domain.game;
/**
 * 데이터 구조 미정
 * since 25.1.7
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndGameUserInfo {
    private String clientId;
    private int rank;
    private int kill;
}
