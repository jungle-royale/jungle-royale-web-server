package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.UserAlreadyInGameException;
import com.example.jungleroyal.common.types.UserRole;
import com.example.jungleroyal.common.types.UserStatus;
import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import com.example.jungleroyal.domain.user.UserDto;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private RandomNicknameGenerator randomNicknameGenerator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        randomNicknameGenerator = mock(RandomNicknameGenerator.class);
        userService = new UserService(userRepository, null, randomNicknameGenerator, null, null);
    }

    @Test
    void 이미_다른_게임에_접속중인_경우_예외를_발생시킨다() {
        // given
        Long userId = 1L;
        String newClientId = "client123";
        String newGameRoomUrl = "room123";
        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        user.setUserStatus(UserStatus.IN_GAME);
        user.setCurrentGameUrl("existingRoom123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.updateUserConnectionDetails(userId, newClientId, newGameRoomUrl))
                .isInstanceOf(UserAlreadyInGameException.class)
                .hasMessageContaining("User is already in a game");
    }

    @Test
    void 사용자ID로_닉네임조회_성공() {
        // given
        String userId = "1";
        Long userIdLong = Long.parseLong(userId);
        String expectedUsername = "testUser";

        when(userRepository.findUsernameById(userIdLong)).thenReturn(expectedUsername);

        // when
        String actualUsername = userService.getUsernameById(userId);

        // then
        assertEquals(expectedUsername, actualUsername, "닉네임이 예상값과 일치해야 합니다.");
        verify(userRepository, times(1)).findUsernameById(userIdLong);
    }

    @Test
    void 닉네임_변경_성공() {
        // given
        Long userId = 1L;
        String oldUsername = "oldTestUser";
        String newUsername = "newTestUser";

        UserJpaEntity userJpaEntity = UserJpaEntity.builder()
                .id(userId)
                .username(oldUsername)
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .username(newUsername)
                .build();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(userJpaEntity));

        // when
        userService.updateNickName(userDto);

        // then
        assertEquals(newUsername, userJpaEntity.getUsername(), "닉네임이 변경되어야 합니다.");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userJpaEntity);
    }

    @Test
    void 닉네임_변경_실패_사용자없음() {
        // given
        Long userId = 999L;
        String newUsername = "newTestUser";

        UserDto userDto = UserDto.builder()
                .id(userId)
                .username(newUsername)
                .build();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateNickName(userDto);
        });

        assertEquals("User not found", exception.getMessage(), "존재하지 않는 사용자의 경우 예외 메시지가 일치해야 합니다.");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }


    @Test
    void 비회원_유저를_생성하고_저장한다() {
        // given
        String randomNickname = "Guest123";
        when(randomNicknameGenerator.generate()).thenReturn(randomNickname);
        UserJpaEntity mockUser = UserJpaEntity.createGueutUser(randomNickname);
        when(userRepository.save(Mockito.any(UserJpaEntity.class))).thenReturn(mockUser);

        // when
        UserJpaEntity createdUser = userService.registerGuest();

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(randomNickname);
        assertThat(createdUser.getRole()).isEqualTo(UserRole.GUEST);
        verify(userRepository, times(1)).save(Mockito.any(UserJpaEntity.class));
    }

    @Test
    void 게임_시작_시_유저_상태를_IN_GAME으로_변경한다() {
        // given
        List<String> userIds = List.of("a", "b", "c");
        List<UserJpaEntity> users = userIds.stream()
                .map(id -> {
                    UserJpaEntity user = new UserJpaEntity();
                    user.setClientId(id);
                    user.setUserStatus(UserStatus.WAITING);
                    return user;
                })
                .toList();

        when(userRepository.findAllById(userIds)).thenReturn(users);

        // when
        userService.updateUsersToInGame(userIds);

        // then
        assertThat(users).allMatch(user -> user.getUserStatus() == UserStatus.IN_GAME);
        verify(userRepository, times(1)).saveAll(users);
    }

    @Test
    void 대기_중이_아닌_유저가_있으면_예외를_발생시킨다() {
        // given
        List<String> clientIds = List.of("a", "b");
        UserJpaEntity waitingUser = new UserJpaEntity();
        waitingUser.setId(1L);
        waitingUser.setUserStatus(UserStatus.WAITING);

        UserJpaEntity inGameUser = new UserJpaEntity();
        inGameUser.setId(2L);
        inGameUser.setUserStatus(UserStatus.IN_GAME);

        when(userRepository.findAllById(clientIds)).thenReturn(List.of(waitingUser, inGameUser));

        // when & then
        assertThatThrownBy(() -> userService.updateUsersToInGame(clientIds))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User is not in WAITING status");
    }

}
