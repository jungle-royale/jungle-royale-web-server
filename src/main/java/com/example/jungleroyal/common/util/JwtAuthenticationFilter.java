package com.example.jungleroyal.common.util;

import com.example.jungleroyal.common.config.BypassUrlConfig;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.service.JwtService;
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
    private final BypassUrlConfig bypassUrlConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 필터 우회 경로 확인
        if (bypassUrlConfig.isBypassUrl(requestUri)) {
            filterChain.doFilter(request, response); // 다음 필터로 진행
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            try{
                if (jwtTokenProvider.isValidToken(jwtToken)) {
                    Long userId = Long.valueOf(jwtTokenProvider.extractSubject(jwtToken));
                    String username = jwtTokenProvider.extractUsername(jwtToken);
                    UserRole userRole = jwtTokenProvider.extractUserRole(jwtToken);

                    CustomUserDetails userDetails = new CustomUserDetails(
                            userId,
                            username,
                            userRole,
                            null // 권한 정보가 필요하면 추가
                    );

                    // 인증 정보를 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Invalid JWT token");
                    handleInvalidToken(response, "INVALID_TOKEN", "Invalid JWT token");
                    return;
                }
            } catch(ExpiredJwtException e) {
                log.warn("Expired JWT token" , e);
                handleInvalidToken(response, "EXPIRED_TOKEN", "JWT token has expired");
                return;
            } catch (Exception e) {
                log.error("Unexpected error with JWT token", e);
                handleInvalidToken(response, "UNKNOWN_ERROR", "Unexpected error with JWT token");
                return;
            }
        } else {
            log.info("No JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }

    // 유효하지 않은 토큰 처리 메서드
    private void handleInvalidToken(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json");

        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        response.getWriter().write("{\"status\":401,\"errorCode\":\"" + errorCode + "\",\"message\":\"" + message + "\"}");
    }
}
