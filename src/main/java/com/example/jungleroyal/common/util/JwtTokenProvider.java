package com.example.jungleroyal.common.util;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.infrastructure.RefreshToken;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final long JWT_EXPIRE_TIME = 1000 * 60 * 60 * 24;       // 24시간
    private static final long JWT_REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;       // 7일

    public static final String TYPE = "Bearer";

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *
     * @param userId
     * @return
     */
    public String generate(String userId, String username, UserRole userRole) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("role", userRole.name())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 리프레시 토큰 생성 메서드
    public RefreshToken generateRefreshToken(Long userId, String username, UserRole userRole) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRE_TIME);

        // Refresh Token은 간단히 subject(사용자 ID)와 만료 시간만 설정
        String refresh = Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID만 포함
                .claim("category", "refresh")
                .claim("username", username)
                .claim("role", userRole.name())
                .setExpiration(expirationDate) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명
                .compact();

        return RefreshToken.builder()
                .refreshToken(refresh)
                .createdAt(LocalDateTime.now())
                .expiresAt(expirationDate)
                .updatedAt(LocalDateTime.now())
                .userId(userId)
                .build();
    }

    public String generateKakaoJwt(String userId, String username, String userRole, String kakaoId) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("kakaoId", kakaoId)
                .claim("role", userRole)
                .setExpiration(expirationDate)
                .setIssuedAt(Date.from(Instant.now())) // 발행 시간
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isValidToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // user ID 추출
    public String extractSubject(String jwt) {
        Claims claims = parseClaims(jwt);
        return claims.getSubject();
    }

    // Kakao ID 추출
    public String extractKakaoId(String token) {
        Claims claims = parseClaims(token);
        return claims.get("kakaoId", String.class);
    }

    public String extractUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public UserRole extractUserRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        System.out.println("role = " + role);
        return UserRole.valueOf(role); // 문자열을 Enum으로 변환
    }

    public Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    // 리프레시 토큰 검증 메서드
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 파싱 및 검증
            return true;
        } catch (Exception e) {
            // 토큰이 유효하지 않음
            return false;
        }
    }
}
