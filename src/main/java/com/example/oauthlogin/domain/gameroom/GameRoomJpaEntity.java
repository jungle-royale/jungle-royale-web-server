package com.example.oauthlogin.domain.gameroom;

import com.example.oauthlogin.common.types.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "gameUrl") // 방 해시의 중복 방지
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRoomJpaEntity {
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
    private Boolean secret;

    @Column(nullable = false)
    private int currentPlayers; // New field for tracking connected players

    @Column(nullable = false)
    private String hostId; // New field for tracking connected players

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.WAITING;

    @Column(nullable = false)
    private String gameUrl;

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
                .gameUrl(gameUrl)
                .build();
    }

    public static GameRoomJpaEntity fromDto(GameRoomDto dto) {
        return GameRoomJpaEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .minPlayers(dto.getMinPlayers())
                .maxPlayers(dto.getMaxPlayers())
                .maxGameTime(dto.getMaxGameTime())
                .map(dto.getMap())
                .currentPlayers(dto.getCurrentPlayers())
                .secret(dto.getSecret())
                .status(dto.getStatus())
                .hostId(dto.getHostId())
                .gameUrl(dto.getGameUrl())
                .build();
    }


}
