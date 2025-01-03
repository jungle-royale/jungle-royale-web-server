package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.inventory.InventoryListReponse;
import com.example.jungleroyal.repository.InventoryRepository;
import com.example.jungleroyal.repository.ItemJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class InbentoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JungleFileUtils fileUtils;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("JWT로 사용자 아이템 조회 성공")
    void getItemsByJwtSuccess() {
        // Given
        String jwt = "Bearer mock.jwt.token";
        String userId = "1";

        // Mock ItemJpaEntity List
        ItemJpaEntity item1 = ItemJpaEntity.builder()
                .itemCode(1L)
                .imageUrl("image1.jpg")
                .build();

        ItemJpaEntity item2 = ItemJpaEntity.builder()
                .itemCode(2L)
                .imageUrl("image2.jpg")
                .build();

        List<ItemJpaEntity> mockItems = List.of(item1, item2);

        // Mock behavior
        when(jwtTokenProvider.extractSubject("mock.jwt.token")).thenReturn(userId);
        when(inventoryRepository.findItemsByUserId(Long.parseLong(userId))).thenReturn(mockItems);
        when(fileUtils.generateImageUrl("image1.jpg", "http://mock.base.url")).thenReturn("http://mock.base.url/image1.jpg");
        when(fileUtils.generateImageUrl("image2.jpg", "http://mock.base.url")).thenReturn("http://mock.base.url/image2.jpg");

        // Set baseUrl value
        inventoryService.baseUrl = "http://mock.base.url";

        // When
        InventoryListReponse response = inventoryService.getItemsByJwt(jwt);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getImageUrl()).isEqualTo("http://mock.base.url/image1.jpg");
        assertThat(response.getItems().get(1).getImageUrl()).isEqualTo("http://mock.base.url/image2.jpg");

        verify(jwtTokenProvider, times(1)).extractSubject("mock.jwt.token");
        verify(inventoryRepository, times(1)).findItemsByUserId(Long.parseLong(userId));
        verify(fileUtils, times(2)).generateImageUrl(anyString(), eq("http://mock.base.url"));
    }
}
