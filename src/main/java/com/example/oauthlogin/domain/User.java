package com.example.oauthlogin.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId; // 카카오 회원번호

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.GUEST; // 유저 기본 타입 : GUEST

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastLoginAt = LocalDateTime.now();

    public User createGueutUser(){
        User guestUser = new User();
        guestUser.setKakaoId("GUEST_" + System.currentTimeMillis()); // GUEST 고유 ID 생성
        guestUser.setUsername("Guest" + guestUser.getKakaoId());
        guestUser.setRole(UserRole.GUEST);
        guestUser.setLastLoginAt(LocalDateTime.now());

        return guestUser;
    }
}
