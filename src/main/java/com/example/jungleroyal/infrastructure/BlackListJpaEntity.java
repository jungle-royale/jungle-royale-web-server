package com.example.jungleroyal.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackListJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invalid_refresh_token")
    private String invalidRefreshToken;

    public BlackListJpaEntity(String invalidRefreshToken) {
        this.invalidRefreshToken = invalidRefreshToken;
    }

    public static BlackListJpaEntity fromToken(String token){
        return BlackListJpaEntity.builder()
                .invalidRefreshToken(token)
                .build();
    }
}
