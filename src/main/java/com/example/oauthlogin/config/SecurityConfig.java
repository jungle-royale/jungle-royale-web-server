package com.example.oauthlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // 모든 요청 인증 없이 허용
            );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        // CorsConfiguration 생성
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // 인증 정보 포함 허용
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 허용할 클라이언트 Origin
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
//        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 허용할 요청 헤더
        config.setAllowCredentials(true); // 인증 정보 포함 허용
        config.addAllowedOriginPattern("*"); // 모든 Origin 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.addAllowedHeader("*"); // 모든 요청 헤더 허용

        // URL 패턴에 CORS 설정 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
