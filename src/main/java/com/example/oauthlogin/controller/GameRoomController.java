package com.example.oauthlogin.controller;

import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.common.util.JwtTokenProvider;
import com.example.oauthlogin.domain.dto.GameRoomDto;
import com.example.oauthlogin.domain.dto.GameRoomRequest;
import com.example.oauthlogin.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class GameRoomController {
    private final RoomService roomService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<String> createRoom(
            @RequestBody GameRoomRequest gameRoomRequest,
            @RequestHeader("Authorization") String authorization) {
        System.out.println("방 생성 로직 호출 ");
        System.out.println("gameRoomRequest = " + gameRoomRequest);

        String jwtToken = authorization.substring(7);

        System.out.println("jwtToken = " + jwtToken);
        Long userId = jwtTokenProvider.extractSubject(jwtToken);

        GameRoomDto createdRoom = roomService.createRoom(GameRoomDto.fromRequest(gameRoomRequest, userId));

        return ResponseEntity.ok("ok");
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<GameRoomDto> updateRoom(
            @PathVariable Long roomId,
            @RequestBody GameRoomDto roomDto) {
        System.out.println("방 수정 로직 호출");
        GameRoomDto updatedRoom = roomService.updateRoom(roomId, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @PutMapping("/update/status/{roomId}")
    public ResponseEntity<Void> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status) {
        roomService.updateRoomStatus(roomId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<GameRoomDto>> listAllRooms() {
        return ResponseEntity.ok(roomService.listAllRooms());
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<GameRoomDto> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.of(roomService.getRoomById(roomId));
    }
}
