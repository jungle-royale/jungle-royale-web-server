package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.service.GameRoomService;
import com.example.jungleroyal.service.GameService;
import com.example.jungleroyal.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final GameRoomService gameRoomService;

    /**
     * 게임 시작 api
     * @param roomUrl
     * @return
     */
    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestParam String roomUrl) {

        gameRoomService.updateRoomStatusByRoomUrl(roomUrl, RoomStatus.RUNNING);
        return ResponseEntity.ok("ok");
    }

    /**
     * 게임에서 나온 유저 정보 업데이트 api
     * @param clientId
     * @return
     */
    @PostMapping("/user/exit")
    public ResponseEntity<String> exitUser(@RequestParam String clientId) {
        // TODO: 유저 정보 업데이트

        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 종료 업데이트 api
     * @param roomUrl
     * @param clientId
     * @return
     */
    @PostMapping("/end")
    public ResponseEntity<String> endGame(
            @RequestParam String roomUrl,
            @RequestParam String clientId) {

        gameRoomService.updateRoomStatusByRoomUrl(roomUrl, RoomStatus.END);
        // TODO : 클라이언트 정보 업데이트
        return ResponseEntity.ok("ok");
    }

}