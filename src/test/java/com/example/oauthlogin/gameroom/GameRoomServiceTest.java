package com.example.oauthlogin.gameroom;

import com.example.oauthlogin.domain.GameRoom;
import com.example.oauthlogin.common.types.RoomStatus;
import com.example.oauthlogin.domain.dto.GameRoomDto;
import com.example.oauthlogin.repository.GameRoomRepository;
import com.example.oauthlogin.service.GameRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class GameRoomServiceTest {
    @Mock
    private GameRoomRepository gameRoomRepository;

    @InjectMocks
    private GameRoomService gameRoomService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 게임방_생성() {
        // given
        GameRoomDto roomDto = GameRoomDto.builder()
                .title("Battle Arena")
                .minPlayers(2)
                .maxPlayers(10)
                .maxGameTime(30)
                .mapInfo("Desert Map")
                .currentPlayers(0)
                .status(RoomStatus.WAITING)
                .build();

        GameRoom room = GameRoom.fromDto(roomDto);

        when(gameRoomRepository.save(any(GameRoom.class))).thenReturn(room);

        // when
        GameRoomDto createdRoom = gameRoomService.createRoom(roomDto);

        // then
        assertThat(createdRoom).isNotNull();
        assertThat(createdRoom.getTitle()).isEqualTo("Battle Arena");
        verify(gameRoomRepository, times(1)).save(any(GameRoom.class));
    }

    @Test
    void 게임방_수정() {
        // given
        GameRoom existingRoom = new GameRoom();
        existingRoom.setId(1L);
        existingRoom.setTitle("Old Arena");

        when(gameRoomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(gameRoomRepository.save(any(GameRoom.class))).thenReturn(existingRoom);

        GameRoomDto updatedRoomDto = GameRoomDto.builder()
                .title("Updated Arena")
                .minPlayers(3)
                .maxPlayers(15)
                .maxGameTime(40)
                .mapInfo("Updated Map")
                .currentPlayers(5)
                .status(RoomStatus.IN_PROGRESS)
                .build();

        // when
        GameRoomDto result = gameRoomService.updateRoom(1L, updatedRoomDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Arena");
        verify(gameRoomRepository, times(1)).findById(1L);
        verify(gameRoomRepository, times(1)).save(existingRoom);
    }

    @Test
    void 게임룸_삭제() {
        // given
        Long roomId = 1L;

        doNothing().when(gameRoomRepository).deleteById(roomId);

        // when
        gameRoomService.deleteRoom(roomId);

        // then
        verify(gameRoomRepository, times(1)).deleteById(roomId);
    }

    @Test
    void 게임룸_전체조회() {
        // given
        GameRoom room1 = new GameRoom();
        room1.setId(1L);
        room1.setTitle("Arena 1");

        GameRoom room2 = new GameRoom();
        room2.setId(2L);
        room2.setTitle("Arena 2");

        when(gameRoomRepository.findAll()).thenReturn(List.of(room1, room2));

        // when
        List<GameRoomDto> rooms = gameRoomService.listAllRooms();

        // then
        assertThat(rooms).hasSize(2);
        verify(gameRoomRepository, times(1)).findAll();
    }

    @Test
    void 게임룸_부분조회() {
        // given
        GameRoom room = new GameRoom();
        room.setId(1L);
        room.setTitle("Arena 1");

        when(gameRoomRepository.findById(1L)).thenReturn(Optional.of(room));

        // when
        Optional<GameRoomDto> result = gameRoomService.getRoomById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Arena 1");
        verify(gameRoomRepository, times(1)).findById(1L);
    }
}
