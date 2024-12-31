package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.shop.ShopPageResponse;

public interface ShopService {
    ShopPageResponse getShopPage(String jwt);
    String purchaseItem(Long userId, Long itemCode);

}
