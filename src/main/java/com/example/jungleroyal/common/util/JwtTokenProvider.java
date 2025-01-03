package com.example.jungleroyal.common.util;

import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.domain.OAuthKakaoToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final long JWT_EXPIRE_TIME = 1000 * 60 * 60;       // 1시간

    public static final String TYPE = "Bearer";

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *
     * @param subject
     * @return
     */
    public String generate(String subject, String username, UserRole userRole) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(subject)
                .claim("username", username)
                .claim("role", userRole.name())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
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
        return UserRole.valueOf(role); // 문자열을 Enum으로 변환
    }


    public Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }




}
