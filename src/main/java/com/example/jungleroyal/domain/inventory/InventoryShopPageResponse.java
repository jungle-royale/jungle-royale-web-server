package com.example.jungleroyal.domain.inventory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryShopPageResponse {
    private Long itemCode;
    private String itemName;
    private Integer itemPrice;
}
