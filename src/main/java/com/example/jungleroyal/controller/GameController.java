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

    @PostMapping("/{roomId}/start")
    public ResponseEntity<String> startGame(@PathVariable String roomUrl) {

        gameRoomService.updateRoomStatusByRoomUrl(roomUrl, RoomStatus.RUNNING);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/{roomId}/end")
    public ResponseEntity<String> endGame(@PathVariable String roomUrl) {

        gameRoomService.updateRoomStatusByRoomUrl(roomUrl, RoomStatus.END);
        return ResponseEntity.ok("ok");
    }

}
