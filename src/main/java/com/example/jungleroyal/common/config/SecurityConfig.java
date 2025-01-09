package com.example.jungleroyal.common.config;

import com.example.jungleroyal.common.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BypassUrlConfig bypassUrlConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
            .authorizeHttpRequests(auth -> {
//                bypassUrlConfig.getBypassUrls().forEach(url -> auth.requestMatchers(url).permitAll());
                auth.anyRequest().permitAll();
//                auth.anyRequest().authenticated();
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 필터 추가;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration 생성
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 인증 정보 포함 허용
        config.addAllowedOriginPattern("*"); // 모든 Origin 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.addAllowedHeader("*"); // 모든 요청 헤더 허용

        // URL 패턴에 CORS 설정 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
