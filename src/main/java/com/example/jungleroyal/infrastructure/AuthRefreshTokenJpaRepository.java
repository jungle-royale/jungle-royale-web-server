package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRefreshTokenJpaRepository extends JpaRepository<AuthRefreshTokenJpaEntity, Long> {

    // 최신 정보를 가져오는 메서드
    AuthRefreshTokenJpaEntity findFirstByUserIdOrderByCreatedAtDesc(long userId);
}
