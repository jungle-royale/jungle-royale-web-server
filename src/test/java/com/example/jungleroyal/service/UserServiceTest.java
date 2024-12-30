package com.example.jungleroyal.service;

import com.example.jungleroyal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
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
}
