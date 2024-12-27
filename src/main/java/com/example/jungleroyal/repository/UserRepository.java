package com.example.jungleroyal.repository;

import com.example.jungleroyal.domain.user.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByKakaoId(String kakaoId);

    @Query("SELECT u.username FROM UserJpaEntity u WHERE u.id = :userId")
    String findUsernameById(@Param("userId") Long userId);
}
