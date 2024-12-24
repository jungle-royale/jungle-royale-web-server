package com.example.oauthlogin.controller;

import com.example.oauthlogin.common.util.JwtTokenProvider;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.service.GameService;
import com.example.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final UserService userService;
    private final GameService gameService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestHeader("Authorization") String authorization) {
        String jwtToken = authorization.substring(7);

        System.out.println("jwtToken = " + jwtToken);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        String kakaoId = userService.getKakaoIdByUserId(userId);

        // 회원 또는 비회원 처리
        User user = userService.findOrRegisterGuest(kakaoId);

        // 게임 참여
        gameService.participateInGame(user);

        return ResponseEntity.ok(user.getUsername() + " has started the game");
    }

}
