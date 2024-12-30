package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.domain.shop.ShopResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.InventoryJpaEntity;
import com.example.jungleroyal.repository.InventoryRepository;
import com.example.jungleroyal.repository.ItemRepository;
import com.example.jungleroyal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService{
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    @Override
    @Transactional
    public ShopResponse getShopPage(String jwt) {
        // JWT에서 유저 ID 추출
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        // 유저 정보 조회
        UserJpaEntity userJpaEntity = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ShopResponse 생성 및 반환
        return ShopResponse.fromUserJpaEntity(userJpaEntity);
    }

    @Override
    @Transactional
    public String purchaseItem(Long userId, Long itemCode) {
        // 유저 조회
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 아이템 조회
        ItemJpaEntity item = itemRepository.findById(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // 유저의 게임머니 확인
        if (user.getGameMoney() < item.getPrice()) {
            throw new IllegalStateException("돈이 부족합니다.");
        }

        // 인벤토리 조회 또는 생성
        InventoryJpaEntity inventory = inventoryRepository.findByUser(user)
                .orElse(InventoryJpaEntity.builder().user(user).build());

        // 아이템 추가
        inventory.addItem(item);

        // 게임머니 차감
        user.setGameMoney(user.getGameMoney() - item.getPrice());

        // 데이터 저장
        inventoryRepository.save(inventory);
        userRepository.save(user);

        return "Item purchased successfully";
    }
}
