package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.exception.GameRoomException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.GameServerClient;
import com.example.jungleroyal.common.util.SecurityUtil;
import com.example.jungleroyal.domain.game.GameServerNotificationRequest;
import com.example.jungleroyal.domain.game.GameServerNotificationResponse;
import com.example.jungleroyal.domain.gameroom.*;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.domain.user.UserInfoUsingRoomListResponse;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.GameRoomService;
import com.example.jungleroyal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameRoomController {
    private final GameRoomService gameRoomService;
    private final UserService userService;
    private final GameServerClient gameServerClient;
    private final SecurityUtil securityUtil;

    @PostMapping("/api/rooms/create")
    public ResponseEntity<GameRoomCreateReponse> createRoom(
            @RequestBody GameRoomRequest gameRoomRequest,
            @RequestHeader("Authorization") String authorization
    ) {
        String userId = securityUtil.getUserId();
        UserDto user = userService.getUserDtoById(Long.parseLong(userId));

        // 방 상태가 WAITING인데 유저 상태가 IN_GAME인 경우 예외 처리
        if (user.getUserStatus() == UserStatus.IN_GAME) {
            throw new GameRoomException("INVALID_USER_STATE", "유저가 현재 다른 게임에 참여중입니다.");
        }

        GameRoomDto room = gameRoomService.createRoom(GameRoomDto.fromRequest(gameRoomRequest, userId));
        log.info("room = " + room);

        String roomUrl = gameRoomService.getRoomUrlById(room.getId());

        int minPlayers = room.getMinPlayers();
        int maxPlayTime = room.getMaxGameTime();

        // 게임 서버와 통신
        GameServerNotificationRequest gameServerNotificationRequest
                = new GameServerNotificationRequest(roomUrl, minPlayers, maxPlayTime);
        log.info("🍎request:" + gameServerNotificationRequest.toString());
        GameServerNotificationResponse gameServerResponse
                = gameServerClient.notifyGameServer(gameServerNotificationRequest, userId);
        log.info("🍎response:" + gameServerResponse.toString());

        // 게임 서버 응답 확인
        if (!gameServerResponse.isSuccess()) {
            log.error("게임 서버 응답 실패");
            gameRoomService.deleteRoomById(room.getId());
            throw new IllegalStateException("게임 서버에서 방 생성을 허용하지 않았습니다.");
        }

        // 유저, 게임룸 정보 갱신
        String clientId = userService.getClientId(); // 새로운 clientId 생성
        userService.updateUserConnectionDetails(Long.parseLong(userId), roomUrl, clientId, true);

        GameRoomCreateReponse response = GameRoomCreateReponse.builder()
                .roomId(roomUrl)
                .clientId(clientId)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/rooms/update/{roomId}")
    public ResponseEntity<String> updateRoom(
            @PathVariable Long roomId,
            @RequestBody GameRoomRequest gameRoomRequest) {
        System.out.println("방 수정 로직 호출");
        GameRoomDto gameRoomDto = GameRoomDto.updateRoomFromRequest(gameRoomRequest, roomId);
        gameRoomService.updateRoom(gameRoomDto);

        return ResponseEntity.ok("ok");
    }

    @PutMapping("/api/rooms/update/status/{roomId}")
    public ResponseEntity<Void> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status) {
        gameRoomService.updateRoomStatus(roomId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/rooms/delete/{gameUrl}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String gameUrl) {
        gameRoomService.deleteRoom(gameUrl);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/rooms/list")
    public ResponseEntity<GameRoomListWithUserReponse> listAllRooms() {
        UserInfoUsingRoomListResponse userInfoUsingRoomListResponse = UserInfoUsingRoomListResponse.createUserInfoUsingRoomListResponse(securityUtil.getUsername());
        List<GameRoomListResponse> responseList = gameRoomService.listOfShowableRoom()
                .stream()
                .map(GameRoomListResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
                .toList();
        GameRoomListWithUserReponse gameRoomListWithUserReponse = GameRoomListWithUserReponse.createGameRoomListWithUserReponse(userInfoUsingRoomListResponse, responseList);
        return ResponseEntity.ok(gameRoomListWithUserReponse);
    }

    @GetMapping("/api/rooms/{roomId}")
    public ResponseEntity<GameRoomResponse> getRoomById(@PathVariable Long roomId) {
        GameRoomDto roomDto = gameRoomService.getRoomByIdOrThrow(roomId);
        GameRoomResponse response = GameRoomResponse.fromDto(roomDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 게임 입장 체크
     * @param roomId
     * @return GameRoomStatus
     */
    @PostMapping("/api/rooms/{roomId}/check")
    public ResponseEntity<GameRoomStatus> checkRoomAvailability(@PathVariable Long roomId) {
        String userId = securityUtil.getUserId();
        System.out.println("게임 입장 가능여부 확인 roomId = " + roomId);
        GameRoomStatus status = gameRoomService.checkRoomAvailability(roomId,userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/api/rooms/{roomId}/join")
    public ResponseEntity<GameRoomJoinReponse> joinGameRoom(
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @PathVariable Long roomId) {

        GameRoomJoinReponse response = gameRoomService.joinGameRoom(roomId, jwt);
        return ResponseEntity.ok(response);
    }
}
