package com.example.jungleroyal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListJpaRepository extends JpaRepository<BlackListJpaEntity, Long> {
    default boolean existsByInvalidRefreshToken(String refreshToken) {
        return findByInvalidRefreshToken(refreshToken).isPresent();
    }
    Optional<BlackListJpaEntity> findByInvalidRefreshToken(String refreshToken);

}
