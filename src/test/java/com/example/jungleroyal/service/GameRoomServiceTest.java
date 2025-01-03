package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.GameRoomException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.gameroom.GameRoomJpaEntity;
import com.example.jungleroyal.repository.GameRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class GameRoomServiceTest {
    @Mock
    private GameRoomRepository gameRoomRepository;

    @InjectMocks
    private GameRoomService gameRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("방 입장 가능 상태 확인 - 정상")
    void checkRoomAvailabilitySuccess() {
        // Given
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        mockRoom.setId(1L);
        mockRoom.setStatus(RoomStatus.WAITING);
        mockRoom.setCurrentPlayers(3);
        mockRoom.setMaxPlayers(5);

        when(gameRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // When
        GameRoomStatus result = gameRoomService.checkRoomAvailability(1L);

        // Then
        assertThat(result).isEqualTo(GameRoomStatus.GAME_JOIN_AVAILABLE);
    }

    @Test
    @DisplayName("방이 존재하지 않을 때 예외 발생")
    void checkRoomAvailabilityRoomNotFound() {
        // Given
        when(gameRoomRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gameRoomService.checkRoomAvailability(1L))
                .isInstanceOf(GameRoomException.class)
                .hasMessageContaining("존재하지 않는 방입니다.");
    }

    @Test
    @DisplayName("게임이 이미 시작된 경우 예외 발생")
    void checkRoomAvailabilityGameAlreadyStarted() {
        // Given
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        mockRoom.setId(1L);
        mockRoom.setStatus(RoomStatus.RUNNING);

        when(gameRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // When & Then
        assertThatThrownBy(() -> gameRoomService.checkRoomAvailability(1L))
                .isInstanceOf(GameRoomException.class)
                .hasMessageContaining("게임이 이미 시작되었습니다.");
    }

    @Test
    @DisplayName("방 정원이 초과된 경우 예외 발생")
    void checkRoomAvailabilityRoomFull() {
        // Given
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        mockRoom.setId(1L);
        mockRoom.setStatus(RoomStatus.WAITING);
        mockRoom.setCurrentPlayers(5);
        mockRoom.setMaxPlayers(5);

        when(gameRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // When & Then
        assertThatThrownBy(() -> gameRoomService.checkRoomAvailability(1L))
                .isInstanceOf(GameRoomException.class)
                .hasMessageContaining("방 정원이 초과되었습니다.");
    }
}
