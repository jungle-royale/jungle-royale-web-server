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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/api/").permitAll() // 인증 필요 없는 경로
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // 모든 OPTIONS 요청에 대해 인증을 요구하지 않음
                    .requestMatchers("/api/users").authenticated()  // 인증이 필요한 경로
                    .anyRequest().permitAll() // 모든 요청 인증 없이 허용
//                    .anyRequest().authenticated()  // 인증 필요
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 필터 추가;

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
