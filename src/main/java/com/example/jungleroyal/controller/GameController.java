package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.game.EndGameRequest;
import com.example.jungleroyal.domain.game.GameReturnResponse;
import com.example.jungleroyal.domain.game.StartGameRequest;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.domain.user.UserDto;
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
    private final JwtTokenProvider jwtTokenProvider;

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
     * 게임 종료 업데이트 api
     * @param endGameRequest
     * @return
     */
    @PostMapping("/api/game/end")
    public ResponseEntity<String> endGame(@RequestBody(required = false) EndGameRequest endGameRequest) {
        gameService.endGame(endGameRequest);

        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 실패 시 유저 상태 복구
     *
     * @param clientIds 복구할 유저 clientId 목록
     * @return 성공 여부
     */
    @PostMapping("/api/game/failure-signal")
    public ResponseEntity<String> handleGameFailureSignal(@RequestBody List<String> clientIds) {
        userService.revertUsersToWaitingByClientIds(clientIds);
        return ResponseEntity.ok("Users reverted to WAITING");
    }

    /**
     * 게임 중이었던 방으로 되돌아가기
     *
     * @return roomUrl, clientId
     */
    @PostMapping("/api/game/return")
    public ResponseEntity<GameReturnResponse> returnGame(@RequestHeader("Authorization") String jwt) {
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        UserDto user = userService.getUserDtoById(Long.parseLong(userId));
        if (user.getUserStatus() != UserStatus.IN_GAME) {
            throw new GameRoomException("USER_NOT_IN_GAME", "유저가 게임에 참여 중이 아니므로 다시 돌아갈 수 없습니다.");
        }

        String currentGameUrl = user.getCurrentGameUrl();

        GameRoomDto gameRoomDto  = gameRoomService.getRoomByGameUrl(currentGameUrl);
        if (gameRoomDto.getStatus() == RoomStatus.END) {
            throw new GameRoomException("GAME_ROOM_ENDED", "이미 종료된 방입니다.");
        }

        String clientId = user.getClientId();

        GameReturnResponse response = GameReturnResponse.create(currentGameUrl, clientId);

        return ResponseEntity.ok(response);
    }
}