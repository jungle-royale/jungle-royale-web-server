package com.example.jungleroyal.domain.shop;

import com.example.jungleroyal.repository.UserJpaEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopResponse {
    private Integer gameMoney;

    // UserJpaEntity를 사용해 ShopResponse를 생성하는 정적 메서드
    public static ShopResponse fromUserJpaEntity(UserJpaEntity userJpaEntity) {
        return ShopResponse.builder()
                .gameMoney(userJpaEntity.getGameMoney())
                .build();
    }
}
