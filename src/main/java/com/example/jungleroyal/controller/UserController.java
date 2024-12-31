package com.example.jungleroyal.controller;


import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.domain.user.UserEditMyPageRequest;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.domain.user.UserMyPageResponse;
import com.example.jungleroyal.repository.UserRepository;
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
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserJpaEntity userJpaEntity) {
        System.out.println("user = " + userJpaEntity.toString());
        userRepository.save(userJpaEntity);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/profile")
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
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        System.out.println("userId = " + userId);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-info")
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

    @GetMapping("/mypage")
    public ResponseEntity<UserMyPageResponse> myPage(@RequestHeader("Authorization") String jwt){
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        String usernameById = userService.getUsernameById(userId);

        UserMyPageResponse response = UserMyPageResponse.builder()
                .username(usernameById)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mypage")
    public ResponseEntity<String> editMyPage(
            @RequestHeader("Authorization") String jwt,
            @RequestBody UserEditMyPageRequest userEditMyPageRequest){
        System.out.println("userEditMyPageRequest = " + userEditMyPageRequest);
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        UserDto userDto = UserDto.fromUserEditMyPageRequest(Long.parseLong(userId), userEditMyPageRequest);
        userService.updateNickName(userDto);

        return ResponseEntity.ok().build();
    }

}
