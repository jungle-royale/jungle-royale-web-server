package com.example.oauthlogin.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // User와 다대일 관계
    @JoinColumn(name = "user_id", nullable = false) // 외래키
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Integer expiresAt;
}
