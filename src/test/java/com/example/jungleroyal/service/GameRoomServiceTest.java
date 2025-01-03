package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.GameRoomException;
import com.example.jungleroyal.common.exceptions.RoomNotFoundException;
import com.example.jungleroyal.common.types.GameRoomStatus;
import com.example.jungleroyal.common.types.RoomStatus;
import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.infrastructure.GameRoomJpaEntity;
import com.example.jungleroyal.infrastructure.GameRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameRoomServiceTest {
    @Mock
    private RedissonClient redissonClient;

    @Mock
    private GameRoomRepository gameRoomRepository;

    @InjectMocks
    private GameRoomService gameRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("방 생성 성공")
    void createRoomSuccess() throws InterruptedException {
        // Given
        GameRoomDto gameRoomDto = GameRoomDto.builder()
                .hostId("testHost")
                .build();

        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock("host:testHost")).thenReturn(mockLock);
        when(mockLock.tryLock(3, 5, TimeUnit.SECONDS)).thenReturn(true);
        when(mockLock.isHeldByCurrentThread()).thenReturn(true); // Mock 동작 추가
        when(gameRoomRepository.existsByHostId("testHost")).thenReturn(false);
        when(gameRoomRepository.save(any(GameRoomJpaEntity.class))).thenReturn(new GameRoomJpaEntity());

        // When
        GameRoomDto result = gameRoomService.createRoom(gameRoomDto);

        // Then
        assertThat(result).isNotNull();
        verify(gameRoomRepository, times(1)).existsByHostId("testHost");
        verify(gameRoomRepository, times(1)).save(any(GameRoomJpaEntity.class));
        verify(mockLock, times(1)).unlock(); // 락 해제 확인
    }

    @Test
    @DisplayName("방 업데이트 성공")
    void updateRoomSuccess() {
        // Given
        GameRoomDto gameRoomDto = GameRoomDto.builder()
                .id(1L)
                .build();
        gameRoomDto.setId(1L);

        when(gameRoomRepository.save(any(GameRoomJpaEntity.class))).thenReturn(new GameRoomJpaEntity());

        // When
        gameRoomService.updateRoom(gameRoomDto);

        // Then
        verify(gameRoomRepository, times(1)).save(any(GameRoomJpaEntity.class));
    }

    @Test
    @DisplayName("방 상태 업데이트 성공")
    void updateRoomStatusSuccess() {
        // Given
        Long roomId = 1L;
        RoomStatus newStatus = RoomStatus.RUNNING;

        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        mockRoom.setId(roomId);
        mockRoom.setStatus(RoomStatus.WAITING);

        when(gameRoomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));

        // When
        gameRoomService.updateRoomStatus(roomId, newStatus);

        // Then
        verify(gameRoomRepository, times(1)).save(mockRoom);
        assertThat(mockRoom.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("방 삭제 성공")
    void deleteRoomSuccess() {
        // Given
        String gameUrl = "testGameUrl";
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();

        when(gameRoomRepository.findByGameUrl(gameUrl)).thenReturn(Optional.of(mockRoom));

        // When
        gameRoomService.deleteRoom(gameUrl);

        // Then
        verify(gameRoomRepository, times(1)).delete(mockRoom);
    }

    @Test
    @DisplayName("모든 방 조회 성공")
    void listAllRoomsSuccess() {
        // Given
        GameRoomJpaEntity room1 = new GameRoomJpaEntity();
        GameRoomJpaEntity room2 = new GameRoomJpaEntity();
        when(gameRoomRepository.findAll()).thenReturn(List.of(room1, room2));

        // When
        List<GameRoomDto> result = gameRoomService.listAllRooms();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("ID로 방 조회 성공")
    void getRoomByIdOrThrowSuccess() {
        // Given
        Long roomId = 1L;
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        when(gameRoomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));

        // When
        GameRoomDto result = gameRoomService.getRoomByIdOrThrow(roomId);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("ID로 방 조회 실패 - 방 없음")
    void getRoomByIdOrThrowNotFound() {
        // Given
        Long roomId = 1L;
        when(gameRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gameRoomService.getRoomByIdOrThrow(roomId))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("ID로 방 삭제 성공")
    void deleteRoomByIdSuccess() {
        // Given
        Long roomId = 1L;
        GameRoomJpaEntity mockRoom = new GameRoomJpaEntity();
        when(gameRoomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));

        // When
        gameRoomService.deleteRoomById(roomId);

        // Then
        verify(gameRoomRepository, times(1)).delete(mockRoom);
    }

    @Test
    @DisplayName("ID로 게임 URL 조회 성공")
    void getRoomUrlByIdSuccess() {
        // Given
        Long roomId = 1L;
        String expectedUrl = "testGameUrl";

        when(gameRoomRepository.getGameUrlById(roomId)).thenReturn(expectedUrl);

        // When
        String result = gameRoomService.getRoomUrlById(roomId);

        // Then
        assertThat(result).isEqualTo(expectedUrl);
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
