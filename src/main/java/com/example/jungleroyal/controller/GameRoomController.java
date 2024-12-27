package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.gameroom.*;
import com.example.jungleroyal.domain.user.UserInfoUsingRoomListResponse;
import com.example.jungleroyal.service.GameRoomService;
import com.example.jungleroyal.service.UserService;
import com.example.jungleroyal.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class GameRoomController {
    private final GameRoomService gameRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;

    @PostMapping("/create")
    public ResponseEntity<GameRoomResponse> createRoom(
            @RequestBody GameRoomRequest gameRoomRequest,
            @RequestHeader("Authorization") String authorization) {

        String jwtToken = authorization.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        GameRoomDto room = gameRoomService.createRoom(GameRoomDto.fromRequest(gameRoomRequest, userId));
        log.info("room = " + room);

        return ResponseEntity.ok(GameRoomResponse.fromDto(room));
    }


    // TODO: 인게임에서 방 속성 변경 시 어떻게 처리할까? 추후 확인할 것
    @PutMapping("/update/{roomId}")
    public ResponseEntity<String> updateRoom(
            @PathVariable Long roomId,
            @RequestBody GameRoomRequest gameRoomRequest) {
        System.out.println("방 수정 로직 호출");
        GameRoomDto gameRoomDto = GameRoomDto.updateRoomFromRequest(gameRoomRequest, roomId);
        gameRoomService.updateRoom(gameRoomDto);

        return ResponseEntity.ok("ok");
    }

    @PutMapping("/update/status/{roomId}")
    public ResponseEntity<Void> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status) {
        gameRoomService.updateRoomStatus(roomId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{gameUrl}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String gameUrl) {
        gameRoomService.deleteRoom(gameUrl);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<GameRoomListWithUserReponse> listAllRooms(@RequestHeader("Authorization") String authorization) {
        String jwtToken = authorization.substring(7);
        log.info("방 호출 시간 :" + System.currentTimeMillis());
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        log.info("토큰으로부터 userId 얻은 시간 :" + System.currentTimeMillis());
        String username = userService.getUsernameById(userId);
        log.info("(DB)유저 이름 얻어오기 완료  :" + System.currentTimeMillis());
        UserInfoUsingRoomListResponse userInfoUsingRoomListResponse = UserInfoUsingRoomListResponse.createUserInfoUsingRoomListResponse(username);
        log.info("UserInfoUsingRoomListResponse 객체 생성 완료  :" + System.currentTimeMillis());

        List<GameRoomListResponse> responseList = gameRoomService.listAllRooms()
                .stream()
                .map(GameRoomListResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
                .toList();
        log.info("룸 리스트 조회 후 리스트화 완료 :" + System.currentTimeMillis());
        GameRoomListWithUserReponse gameRoomListWithUserReponse = GameRoomListWithUserReponse.createGameRoomListWithUserReponse(userInfoUsingRoomListResponse, responseList);
        log.info("GameRoomListWithUserReponse 생성 완료   :" + System.currentTimeMillis());
        return ResponseEntity.ok(gameRoomListWithUserReponse);
    }

    @GetMapping("/{roomId}")
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
    @PostMapping("/{roomId}/check")
    public ResponseEntity<GameRoomStatus> checkRoomAvailability(@PathVariable Long roomId) {
        System.out.println("게임 입장 가능여부 확인 roomId = " + roomId);
        GameRoomStatus status = gameRoomService.checkRoomAvailability(roomId);
        return ResponseEntity.ok(status);
    }
}
