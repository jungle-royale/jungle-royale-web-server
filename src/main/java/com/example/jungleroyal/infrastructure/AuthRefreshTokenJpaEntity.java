package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.types.AuthType;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.dto.AuthTokensDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRefreshTokenJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private LocalDateTime updatedAt = TimeUtils.createUtc();
    private LocalDateTime createdAt = TimeUtils.createUtc();

    public void updateUpdatedAt(LocalDateTime utc) {
        this.updatedAt = utc;
    }

    public AuthTokensDto toTokensDto() {
        return AuthTokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authType(authType)
                .build();
    }
}
