package com.example.jungleroyal.service;

import com.example.jungleroyal.common.exceptions.MoneyInsufficientException;
import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.inventory.InventoryShopPageResponse;
import com.example.jungleroyal.repository.ItemJpaEntity;
import com.example.jungleroyal.domain.item.ItemShopPageResponse;
import com.example.jungleroyal.domain.shop.ShopPageResponse;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.domain.user.UserShopPageResponse;
import com.example.jungleroyal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final JungleFileUtils fileUtils;

    @Value("${base.url.shop}")
    private String baseUrl;
    /**
     *
     * 상점페이지 조회
     *
     * @param jwt
     * @return
     */
    @Override
    @Transactional
    public ShopPageResponse getShopPage(String jwt) {
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        UserJpaEntity userJpaEntity = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserShopPageResponse userShopPageResponse = UserShopPageResponse.fromUserJpaEntity(userJpaEntity);

        List<ItemShopPageResponse> items = itemRepository.findAll().stream()
                .map(item -> ItemShopPageResponse.builder()
                        .itemCode(item.getItemCode())
                        .name(item.getName())
                        .price(item.getPrice())
                        .imageUrl(fileUtils.generateImageUrl(item.getImageUrl(), baseUrl))
                        .build())
                .collect(Collectors.toList());

        List<InventoryShopPageResponse> inventory = ownedItemRepository.findOwnedItemsByUserId(userJpaEntity.getId()).stream()
                .map(item -> InventoryShopPageResponse.builder()
                        .itemCode(item.getTemplateItemCode())
                        .itemName(item.getName())
                        .itemPrice(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return ShopPageResponse.builder()
                .inventory(inventory)
                .userInfo(userShopPageResponse)
                .items(items)
                .build();

    }

    @Override
    @Transactional
    public String purchaseItem(Long userId, Long itemCode) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ItemJpaEntity shopItem = itemRepository.findById(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        InventoryJpaEntity inventory = inventoryRepository.findByUser(user)
                .orElseGet(() -> {
                    InventoryJpaEntity newInventory = InventoryJpaEntity.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return inventoryRepository.save(newInventory);
                });

        if (user.getGameMoney() < shopItem.getPrice()) {
            throw new MoneyInsufficientException(userId, user.getGameMoney(), shopItem.getPrice());
        }

        OwnedItemJpaEntity ownedItem = OwnedItemJpaEntity.builder()
                .inventory(inventory)
                .name(shopItem.getName())
                .price(shopItem.getPrice())
                .imageUrl(shopItem.getImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .templateItemCode(shopItem.getItemCode())
                .build();

        ownedItemRepository.save(ownedItem);

        user.setGameMoney(user.getGameMoney() - shopItem.getPrice());

        inventoryRepository.save(inventory);
        userRepository.save(user);

        return "Item purchased successfully";
    }
}
