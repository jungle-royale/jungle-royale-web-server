package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.shop.ShopResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ShopServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ShopServiceImpl shopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유저가_존재하지_않을_때_예외를_던진다() {
        // given
        String jwt = "Bearer test-token";
        String userId = "1";

        when(jwtTokenProvider.extractSubject(anyString())).thenReturn(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when / then
        try {
            shopService.getShopPage(jwt);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(jwtTokenProvider, times(1)).extractSubject(jwt.substring(7));
        verify(userRepository, times(1)).findById(1L);
    }
}
