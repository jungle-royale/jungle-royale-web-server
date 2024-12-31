package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.MoneyInsufficientException;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.inventory.InventoryShopPageResponse;
import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.domain.item.ItemShopPageResponse;
import com.example.jungleroyal.domain.shop.ShopPageResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.domain.user.UserShopPageResponse;
import com.example.jungleroyal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService{
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final OwnedItemRepository ownedItemRepository;
    @Override
    @Transactional
    public ShopPageResponse getShopPage(String jwt) {
        // JWT에서 유저 ID 추출
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        // 유저 정보 조회
        UserJpaEntity userJpaEntity = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserShopPageResponse userShopPageResponse = UserShopPageResponse.fromUserJpaEntity(userJpaEntity);

        // 아이템 목록 조회
        List<ItemShopPageResponse> items = itemRepository.findAll().stream()
                .map(item -> ItemShopPageResponse.builder()
                        .itemCode(item.getItemCode())
                        .name(item.getName())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        List<InventoryShopPageResponse> inventory = ownedItemRepository.findOwnedItemsByUserId(userJpaEntity.getId()).stream()
                .map(item -> InventoryShopPageResponse.builder()
                        .itemCode(item.getTemplateItemCode())
                        .itemName(item.getName())
                        .itemPrice(item.getPrice())
                        .build())
                .collect(Collectors.toList());


//        // 유저 인벤토리 조회
//        List<InventoryShopPageResponse> inventory = inventoryRepository.findItemsByUserId(userJpaEntity.getId()).stream()
//                .map(item -> InventoryShopPageResponse.builder()
//                        .itemCode(item.getItemCode())
//                        .itemName(item.getName())
//                        .itemPrice(item.getPrice())
//                        .build())
//                .collect(Collectors.toList());

        // ShopPageResponse 생성
        return ShopPageResponse.builder()
                .inventory(inventory)
                .userInfo(userShopPageResponse)
                .items(items)
                .build();

    }

    @Override
    @Transactional
    public String purchaseItem(Long userId, Long itemCode) {
        // 유저 조회
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 아이템 조회
        ItemJpaEntity shopItem = itemRepository.findById(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // 유저 인벤토리 조회 또는 생성
        InventoryJpaEntity inventory = inventoryRepository.findByUser(user)
                .orElseGet(() -> {
                    InventoryJpaEntity newInventory = InventoryJpaEntity.builder()
                            .user(user)
                            .build();
                    return inventoryRepository.save(newInventory); // 인벤토리 먼저 저장
                });

        // 유저의 게임머니 확인
        if (user.getGameMoney() < shopItem.getPrice()) {
            throw new MoneyInsufficientException(userId, user.getGameMoney(), shopItem.getPrice());
        }

        // 소유 아이템 생성
        OwnedItemJpaEntity ownedItem = OwnedItemJpaEntity.builder()
                .inventory(inventory)
                .name(shopItem.getName())
                .price(shopItem.getPrice())
                .imageUrl(shopItem.getImageUrl())
                .templateItemCode(shopItem.getItemCode())
                .build();

        // 소유 아이템 저장
        ownedItemRepository.save(ownedItem);

        // 게임머니 차감
        user.setGameMoney(user.getGameMoney() - shopItem.getPrice());

        // 데이터 저장
        inventoryRepository.save(inventory);
        userRepository.save(user);

        return "Item purchased successfully";
    }
}
