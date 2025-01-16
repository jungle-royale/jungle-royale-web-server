package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.domain.game.EndGameRequest;
import com.example.jungleroyal.domain.game.GameReturnResponse;
import com.example.jungleroyal.domain.game.LeaveRoomRequest;
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
    private final SecurityUtil securityUtil;

    /**
     * 게임 시작 api
     * @param  startGameRequest
     * @return
     */
    @PostMapping("/api/game/start")
    public ResponseEntity<String> startGame(@RequestBody StartGameRequest startGameRequest) {
        log.info("🔥게임 시작 요청 - roomId: {}, clientIds: {}", startGameRequest.getRoomId(), startGameRequest.getClientIds());

        String roomId = startGameRequest.getRoomId(); //😎 변경 대상
        gameRoomService.updateRoomStatusByRoomUrl(Long.valueOf(roomId), RoomStatus.RUNNING); //😎 수정 대상
        userService.updateUsersToInGame(startGameRequest.getClientIds());

        log.info("🔥게임 시작 완료 - roomId: {}", roomId);
        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 종료 업데이트 api
     * @param endGameRequest
     * @return
     */
    @PostMapping("/api/game/end")
    public ResponseEntity<String> endGame(@RequestBody(required = false) EndGameRequest endGameRequest) {
        log.info("🔥게임 종료 요청 - roomId: {}", (endGameRequest != null ? endGameRequest.getRoomId() : "null"));
        gameService.endGame(endGameRequest);

        log.info("🔥게임 종료 처리 완료 - roomId: {}", (endGameRequest != null ? endGameRequest.getRoomId() : "null"));
        return ResponseEntity.ok("ok");
    }

    /**
     * 게임 대기 방에서 유저가 나온 경우 로직 처리
     *
     *
     */
    @PostMapping("/api/game/leave")
    public ResponseEntity<String> leave(@RequestBody LeaveRoomRequest leaveRoomRequest) {
        log.info("🔥유저 방 나가기 요청 - roomId: {}, clientId: {}", leaveRoomRequest.getRoomId(), leaveRoomRequest.getClientId());
        gameService.leaveRoom(leaveRoomRequest);
        log.info("🔥유저 방 나가기 처리 완료 - roomId: {}, clientId: {}", leaveRoomRequest.getRoomId(), leaveRoomRequest.getClientId());

        return ResponseEntity.ok("해당 유저가 방을 나갔습니다.");
    }

    /**
     * 게임 실패 시 유저 상태 복구
     *
     * @param clientIds 복구할 유저 clientId 목록
     * @return 성공 여부
     */
    @PostMapping("/api/game/failure-signal")
    public ResponseEntity<String> handleGameFailureSignal(@RequestBody List<String> clientIds) {
        log.info("🔥게임 실패 신호 처리 요청 - clientIds: {}", clientIds);

        userService.revertUsersToWaitingByClientIds(clientIds);
        log.info("🔥게임 실패 신호 처리 완료 - 복구된 clientIds: {}", clientIds);

        return ResponseEntity.ok("Users reverted to WAITING");
    }

    /**
     * 게임 중이었던 방으로 되돌아가기
     *
     * @return roomUrl, clientId
     */
    @PostMapping("/api/game/return")
    public ResponseEntity<GameReturnResponse> returnGame(@RequestHeader(value = "Authorization", required = false) String jwt) {
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        GameReturnResponse response = gameRoomService.returnGame(userId);

        log.info("🔥게임 되돌아가기 처리 완료 - roomUrl: {}, clientId: {}, username : {}", response.getRoomId(), response.getClientId(), response.getUsername());

        return ResponseEntity.ok(response);
    }
}