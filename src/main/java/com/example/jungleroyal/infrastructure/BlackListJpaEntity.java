package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.util.TimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Table(name = "black_list")
@NoArgsConstructor
@Builder
public class BlackListJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invalid_refresh_token")
    private String invalidRefreshToken;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public static BlackListJpaEntity createBlackList(String token){
        return BlackListJpaEntity.builder()
                .invalidRefreshToken(token)
                .createdAt(TimeUtils.createUtc())
                .updatedAt(TimeUtils.createUtc())
                .build();
    }
}
