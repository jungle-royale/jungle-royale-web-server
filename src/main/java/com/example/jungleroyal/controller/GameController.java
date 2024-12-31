package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.service.GameService;
import com.example.jungleroyal.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final UserServiceImpl userService;
    private final GameService gameService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestHeader("Authorization") String authorization) {
        String jwtToken = authorization.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        String kakaoId = userService.getKakaoIdByUserId(userId);

        // 회원 또는 비회원 처리
        UserJpaEntity userJpaEntity = userService.findOrRegisterGuest(kakaoId);

        // 게임 참여
        gameService.participateInGame(userJpaEntity);

        return ResponseEntity.ok(userJpaEntity.getUsername() + " has started the game");
    }

}
