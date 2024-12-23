package com.example.oauthlogin.domain;

import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.dto.GameRoomDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "game_rooms")
@Data
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int minPlayers;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int maxGameTime; // in minutes

    @Column(nullable = false)
    private String map;

    @Column(nullable = false)
    private boolean secret;

    @Column(nullable = false)
    private int currentPlayers; // New field for tracking connected players

    @Column(nullable = false)
    private Long hostId; // New field for tracking connected players

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.WAITING;

    public GameRoomDto toDto() {
        return GameRoomDto.builder()
                .id(id)
                .title(title)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .maxGameTime(maxGameTime)
                .map(map)
                .currentPlayers(currentPlayers)
                .secret(secret)
                .status(status)
                .hostId(hostId)
                .build();
    }

    public static GameRoom fromDto(GameRoomDto dto) {
        GameRoom room = new GameRoom();
        room.setTitle(dto.getTitle());
        room.setMinPlayers(dto.getMinPlayers());
        room.setMaxPlayers(dto.getMaxPlayers());
        room.setMaxGameTime(dto.getMaxGameTime());
        room.setMap(dto.getMap());
        room.setCurrentPlayers(dto.getCurrentPlayers());
        room.setSecret(dto.isSecret());
//        room.setSecret(dto.getSecret());
        room.setStatus(dto.getStatus());
        room.setHostId(dto.getHostId());
        return room;
    }

    public void updateFromDto(GameRoomDto dto) {
        this.title = dto.getTitle();
        this.minPlayers = dto.getMinPlayers();
        this.maxPlayers = dto.getMaxPlayers();
        this.maxGameTime = dto.getMaxGameTime();
        this.map = dto.getMap();
        this.secret = dto.isSecret();
        this.currentPlayers = dto.getCurrentPlayers();
        this.status = dto.getStatus();
    }
}

