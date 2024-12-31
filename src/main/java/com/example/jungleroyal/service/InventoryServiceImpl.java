package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JungleFileUtils fileUtils;

    private static final String baseUrl = "http://192.168.1.241:8080/uploads/items";

    @Override
    @Transactional(readOnly = true)
    public List<ItemJpaEntity> getItemsByJwt(String jwt) {
        String jwtToken = jwt.substring(7);

        // JWT에서 사용자 ID 추출
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        List<ItemJpaEntity> itemsByUserId = inventoryRepository.findItemsByUserId(Long.parseLong(userId));
        for (ItemJpaEntity itemJpaEntity : itemsByUserId) {
            String imageUrl = fileUtils.generateImageUrl(itemJpaEntity.getImageUrl(), baseUrl);
            itemJpaEntity.setImageUrl(imageUrl);
        }

        // 사용자 ID를 이용해 아이템 목록 조회
        return itemsByUserId;
    }
}
