package com.example.oauthlogin.repository;

import com.example.oauthlogin.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    default boolean existsByInvalidRefreshToken(String refreshToken) {
        return findByInvalidRefreshToken(refreshToken).isPresent();
    }
    Optional<BlackList> findByInvalidRefreshToken(String refreshToken);
}
