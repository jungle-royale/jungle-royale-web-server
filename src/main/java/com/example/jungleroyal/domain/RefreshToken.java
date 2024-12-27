package com.example.jungleroyal.domain;

import com.example.jungleroyal.domain.user.UserJpaEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // User와 다대일 관계
    @JoinColumn(name = "user_id", nullable = false) // 외래키
    private UserJpaEntity userJpaEntity;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Integer expiresAt;
}
