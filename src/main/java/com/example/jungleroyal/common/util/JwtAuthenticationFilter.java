package com.example.jungleroyal.common.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 특정 경로들에 대해 필터 로직을 건너뛰도록 설정
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            // OPTIONS 요청일 경우 필터 처리를 건너뛰고 다음 필터로 진행
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("authorizationHeader = " + authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            System.out.println("jwtToken = " + jwtToken);
            try{
                if (jwtTokenProvider.isValidToken(jwtToken)) {
                    Long userId = Long.valueOf(jwtTokenProvider.extractSubject(jwtToken));
//                // JWT 만료가 임박했는지 확인 (1분 이하로 남았을 경우 새로 발급)
//                if (jwtTokenProvider.isTokenExpiringSoon(jwtToken, 60 * 1000)) {
//                    String newJwtToken = jwtTokenProvider.generate(userId, new Date(System.currentTimeMillis() + 3600 * 1000));
//                    response.setHeader("Authorization", "Bearer " + newJwtToken); // 새로운 JWT를 헤더에 추가
//                }

                    // 인증 정보를 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, null);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Invalid JWT token");
                }
            } catch(ExpiredJwtException e) {
                log.warn("Expired JWT token" , e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token has expired");
                return;
            }
        } else {
            log.info("No JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }
}
