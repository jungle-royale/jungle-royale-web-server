package com.example.jungleroyal.service;

import com.example.jungleroyal.infrastructure.BlackListJpaEntity;
import com.example.jungleroyal.service.repository.BlackListRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final BlackListRepository blackListRepository;
    public void invalidateToken(String refreshToken) {
        if (isTokenValid(refreshToken)) {
            // 블랙리스트에 토큰 저장

            blackListRepository.save(BlackListJpaEntity.fromToken(refreshToken));
        } else {
            throw new RuntimeException("Invalid JWT Refresh token.");
        }
    }

    public boolean isTokenValid(String accessToken) {
        try {
            // JWT 파싱 및 검증
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 키 설정
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSigningKey() {
        // JWT 서명 키 반환
        return Keys.hmacShaKeyFor("your-secret-key".getBytes(StandardCharsets.UTF_8));
    }
}
