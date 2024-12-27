package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class UserJpaEntity {

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

    public static UserJpaEntity createGueutUser(String randomNickname){
        UserJpaEntity guestUserJpaEntity = new UserJpaEntity();
        guestUserJpaEntity.setKakaoId("GUEST_" + System.currentTimeMillis()); // GUEST 고유 ID 생성
        guestUserJpaEntity.setUsername(randomNickname);
        guestUserJpaEntity.setRole(UserRole.GUEST);
        guestUserJpaEntity.setLastLoginAt(LocalDateTime.now());

        return guestUserJpaEntity;
    }

    public UserJpaEntity createKakaoUser(String kakaoId, String username){
        UserJpaEntity kakaoUserJpaEntity = new UserJpaEntity();
        kakaoUserJpaEntity.setKakaoId(kakaoId); // GUEST 고유 ID 생성
        kakaoUserJpaEntity.setUsername(username);
        kakaoUserJpaEntity.setRole(UserRole.MEMBER);
        kakaoUserJpaEntity.setLastLoginAt(LocalDateTime.now());

        return kakaoUserJpaEntity;
    }
}
