package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.user.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column
    private String currentGameUrl; // 현재 위치하고있는 게임룸 url

    @Column
    private String clientId; // 방에 접속할 때 사용할 clientId

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;  // 현재 유저 상태 : WAITING || IN_GAME

    // 게임머니 필드 추가
    @Column(nullable = false)
    private Integer gameMoney = 100000000; // 기본값 0

    public static UserJpaEntity createGueutUser(String randomNickname){

        return UserJpaEntity.builder()
                .kakaoId("GUEST_" + System.currentTimeMillis())
                .username(randomNickname)
                .role(UserRole.GUEST)
                .userStatus(UserStatus.WAITING)
                .createdAt(TimeUtils.createUtc())
                .updatedAt(TimeUtils.createUtc())
                .lastLoginAt(TimeUtils.createUtc())
                .build();
    }

    public static UserDto toDto(UserJpaEntity userJpaEntity) {
        return UserDto.builder()
                .id(userJpaEntity.getId())
                .kakaoId(userJpaEntity.getKakaoId())
                .username(userJpaEntity.getUsername())
                .userRole(userJpaEntity.getRole())
                .userStatus(userJpaEntity.getUserStatus())
                .currentGameUrl(userJpaEntity.getCurrentGameUrl())
                .clientId(userJpaEntity.getClientId())
                .createdAt(userJpaEntity.createdAt)
                .updatedAt(userJpaEntity.updatedAt)
                .gameMoney(userJpaEntity.getGameMoney())
                .lastLoginAt(userJpaEntity.lastLoginAt)
                .build();
    }

    public UserJpaEntity createKakaoUser(String kakaoId, String username){
        return UserJpaEntity.builder()
                .kakaoId(kakaoId)
                .username(username)
                .role(UserRole.MEMBER)
                .userStatus(UserStatus.WAITING)
                .createdAt(TimeUtils.createUtc())
                .updatedAt(TimeUtils.createUtc())
                .lastLoginAt(TimeUtils.createUtc())
                .build();
    }
}
