package com.example.oauthlogin.common.util;

import com.example.oauthlogin.domain.OAuthKakaoToken;
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
    public String generate(String subject) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateKakaoJwt(String userId, String kakaoId, OAuthKakaoToken oAuthKakaoToken) {
        Date expirationDate = new Date(System.currentTimeMillis() + oAuthKakaoToken.getExpires_in() * 1000L);

        return Jwts.builder()
                .setSubject(userId)
                .claim("kakaoId", kakaoId)
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


    public Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    public String generateToken(String subject, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * JWT 남은 유효기간 확인
     */
    public boolean isTokenExpiringSoon(String token, long thresholdMillis) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        long timeRemaining = expiration.getTime() - System.currentTimeMillis();

        return timeRemaining < thresholdMillis;
    }
}
