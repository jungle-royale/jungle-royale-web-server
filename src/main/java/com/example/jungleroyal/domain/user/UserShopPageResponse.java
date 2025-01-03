package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.infrastructure.UserJpaEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShopPageResponse {
    private Integer gameMoney;
    private String username;

    public static UserShopPageResponse fromUserJpaEntity(UserJpaEntity userJpaEntity){
        return UserShopPageResponse.builder()
                .username(userJpaEntity.getUsername())
                .gameMoney(userJpaEntity.getGameMoney())
                .build();

    }
}
