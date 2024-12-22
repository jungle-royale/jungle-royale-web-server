package com.example.oauthlogin.repository;

import com.example.oauthlogin.domain.RefreshToken;
import com.example.oauthlogin.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);


}
