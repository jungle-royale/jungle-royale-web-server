package com.example.oauthlogin.controller;

import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.common.util.JwtTokenProvider;
import com.example.oauthlogin.domain.gameroom.GameRoomDto;
import com.example.oauthlogin.domain.gameroom.GameRoomRequest;
import com.example.oauthlogin.domain.gameroom.GameRoomResponse;
import com.example.oauthlogin.service.GameRoomService;
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
    public ResponseEntity<List<GameRoomResponse>> listAllRooms() {
        List<GameRoomResponse> responseList = gameRoomService.listAllRooms()
                .stream()
                .map(GameRoomResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
                .toList();

        for (GameRoomResponse gameRoomResponse : responseList) {
            System.out.println("gameRoomResponse = " + gameRoomResponse);
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<GameRoomResponse> getRoomById(@PathVariable Long roomId) {
        GameRoomDto roomDto = gameRoomService.getRoomByIdOrThrow(roomId);
        GameRoomResponse response = GameRoomResponse.fromDto(roomDto);
        return ResponseEntity.ok(response);
    }
}
