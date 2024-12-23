package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.RoomStatus;
import com.example.oauthlogin.domain.dto.GameRoomDto;
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

    @PostMapping("/create")
    public ResponseEntity<GameRoomDto> createRoom(@RequestBody GameRoomDto roomDto) {
        System.out.println("방 생성 로직 호출 ");
        GameRoomDto createdRoom = roomService.createRoom(roomDto);
        return ResponseEntity.ok(createdRoom);
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
