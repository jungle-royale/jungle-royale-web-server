package com.example.jungleroyal.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateResponse {
    private Long itemCode;
    private String name;
    private Integer price;
    private String imageUrl;
}
