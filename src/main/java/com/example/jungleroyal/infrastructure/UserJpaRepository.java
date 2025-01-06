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

    List<UserJpaEntity> findAllByClientIds(List<String> clientIds);

}
