package com.example.jungleroyal.controller;


import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.domain.user.UserEditMyPageRequest;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.domain.user.UserMyPageResponse;
import com.example.jungleroyal.infrastructure.UserJpaRepository;
import com.example.jungleroyal.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserJpaRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;
    @PostMapping("/api/users/register")
    public ResponseEntity<String> registerUser(@RequestBody UserJpaEntity userJpaEntity) {
        userRepository.save(userJpaEntity);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/api/users/profile")
    public ResponseEntity<UserJpaEntity> getUserById(@RequestHeader("Authorization") String jwt) {
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        return userRepository.findById(Long.parseLong(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * 현재 사용자 추출
     */
    @GetMapping("/api/users/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/current-info")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new IllegalStateException("No authentication found");
        }

        String jwtToken = (String) authentication.getCredentials(); // JWT 토큰 추출
        if (!jwtTokenProvider.isValidToken(jwtToken)) {
            throw new IllegalStateException("Invalid JWT token");
        }

        String kakaoId = jwtTokenProvider.extractKakaoId(jwtToken);

        Map<String, Object> response = new HashMap<>();
        response.put("kakaoId", kakaoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/mypage")
    public ResponseEntity<UserMyPageResponse> myPage(@RequestHeader("Authorization") String jwt){
        log.info("🔥마이페이지조회 : {}", securityUtil.getUsername());
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        UserDto user = userService.getUserDtoById(Long.parseLong(userId));
        log.info("🔥기프트url : {}", user.getGiftImageUrl());

        UserMyPageResponse response = UserMyPageResponse.builder()
                .username(user.getUsername())
                .gift(user.getGiftImageUrl())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/users/mypage")
    public ResponseEntity<String> editMyPage(
            @RequestHeader("Authorization") String jwt,
            @RequestBody UserEditMyPageRequest userEditMyPageRequest){
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        UserDto userDto = UserDto.fromUserEditMyPageRequest(Long.parseLong(userId), userEditMyPageRequest);
        userService.updateNickName(userDto);

        return ResponseEntity.ok().build();
    }

}
