package com.example.jungleroyal.domain.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemShopPageResponse {
    private Long itemCode;
    private String name;
    private Integer price;
    private String imageUrl;
}
