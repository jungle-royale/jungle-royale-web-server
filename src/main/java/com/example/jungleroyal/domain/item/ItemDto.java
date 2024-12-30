package com.example.jungleroyal.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Integer itemCode;
    private String name;
    private Integer price;
    private String imageUrl;


    public static ItemDto from(ItemJpaEntity itemJpaEntity) {
        return ItemDto.builder()
                .itemCode(itemJpaEntity.getItemCode())
                .name(itemJpaEntity.getName())
                .price(itemJpaEntity.getPrice())
                .imageUrl(itemJpaEntity.getImageUrl())
                .build();
    }
}
