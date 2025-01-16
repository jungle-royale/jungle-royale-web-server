package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByKakaoId(String kakaoId);

    @Query("SELECT u.username FROM UserJpaEntity u WHERE u.id = :userId")
    String findUsernameById(@Param("userId") Long userId);

    List<UserJpaEntity> findAllByClientIdIn(List<String> clientIds);

    Optional<UserJpaEntity> findByClientId(String clientId);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.score > 0 AND u.id NOT IN :adminIds ORDER BY u.score DESC, u.username ASC")
    List<UserJpaEntity> findTop100ByScoreExcludeAdmins(@Param("adminIds") List<Long> adminIds);
}
