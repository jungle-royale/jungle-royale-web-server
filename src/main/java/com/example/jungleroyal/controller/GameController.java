package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.game.EndGameRequest;
import com.example.jungleroyal.domain.game.StartGameRequest;
import com.example.jungleroyal.service.GameRoomService;
import com.example.jungleroyal.service.GameService;
import com.example.jungleroyal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameRoomService gameRoomService;
    private final UserService userService;
    private final GameService gameService;

    /**
     * 게임 시작 api
     * @param  startGameRequest
     * @return
     */
    @PostMapping("/api/game/start")
    public ResponseEntity<String> startGame(@RequestBody StartGameRequest startGameRequest) {
        String roomId = startGameRequest.getRoomId();
        gameRoomService.updateRoomStatusByRoomUrl(roomId, RoomStatus.RUNNING);
        userService.updateUsersToInGame(startGameRequest.getClientIds());
        return ResponseEntity.ok("ok");
    }

    /**
     * 게임에서 나온 유저 정보 업데이트 api
     * @param clientId
     * @return
     */
    @PostMapping("/api/game/user/exit")
    public ResponseEntity<String> exitUser(@RequestParam String clientId) {
        // TODO: 유저 정보 업데이트

        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 종료 업데이트 api
     * @param endGameRequest
     * @return
     */
    @PostMapping("/api/game/end")
    public ResponseEntity<String> endGame(@RequestBody EndGameRequest endGameRequest) {
        String roomId = endGameRequest.getRoomId(); // roomId 추출

        gameService.endGame(roomId, endGameRequest.getRankings());

        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 실패 시 유저 상태 복구
     *
     * @param clientIds 복구할 유저 clientId 목록
     * @return 성공 여부
     */
    @PostMapping("/failure-signal")
    public ResponseEntity<String> handleGameFailureSignal(@RequestBody List<String> clientIds) {
        userService.revertUsersToWaitingByClientIds(clientIds);
        return ResponseEntity.ok("Users reverted to WAITING");
    }

}