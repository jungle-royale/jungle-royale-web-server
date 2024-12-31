package com.example.jungleroyal.domain.item;

import com.example.jungleroyal.repository.ItemJpaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long itemCode;
    private String name;
    private Integer price;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static ItemDto from(ItemJpaEntity itemJpaEntity) {
        return ItemDto.builder()
                .itemCode(itemJpaEntity.getItemCode())
                .name(itemJpaEntity.getName())
                .price(itemJpaEntity.getPrice())
                .imageUrl(itemJpaEntity.getImageUrl())
                .createdAt(itemJpaEntity.getCreatedAt())
                .updatedAt(itemJpaEntity.getUpdatedAt())
                .build();
    }
}
