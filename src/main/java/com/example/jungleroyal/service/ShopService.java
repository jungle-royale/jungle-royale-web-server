package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.shop.ShopResponse;

public interface ShopService {
    ShopResponse getShopPage(String jwt);
}
