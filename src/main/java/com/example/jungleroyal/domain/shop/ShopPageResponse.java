package com.example.jungleroyal.domain.shop;

import com.example.jungleroyal.domain.inventory.InventoryShopPageResponse;
import com.example.jungleroyal.domain.item.ItemShopPageResponse;
import com.example.jungleroyal.domain.user.UserShopPageResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShopPageResponse {
    private UserShopPageResponse userInfo;
    private List<ItemShopPageResponse> items;
    private List<InventoryShopPageResponse> inventory;
}
