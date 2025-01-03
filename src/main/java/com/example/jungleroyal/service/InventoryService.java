package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.inventory.InventoryListReponse;
import com.example.jungleroyal.repository.InventoryRepository;
import com.example.jungleroyal.repository.ItemJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JungleFileUtils fileUtils;

    @Value("${base.url.item}")
    private String baseUrl;

    @Transactional(readOnly = true)
    public InventoryListReponse getItemsByJwt(String jwt) {
        String jwtToken = jwt.substring(7);

        // JWT에서 사용자 ID 추출
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        List<ItemJpaEntity> itemsByUserId = inventoryRepository.findItemsByUserId(Long.parseLong(userId));
        for (ItemJpaEntity itemJpaEntity : itemsByUserId) {
            String imageUrl = fileUtils.generateImageUrl(itemJpaEntity.getImageUrl(), baseUrl);
            itemJpaEntity.setImageUrl(imageUrl);
        }

        return InventoryListReponse.builder()
                .items(itemsByUserId)
                .build();
    }
}
