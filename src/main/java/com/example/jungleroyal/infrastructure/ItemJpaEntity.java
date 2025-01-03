package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = true)
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItemJpaEntity from(ItemCreateRequest request) {
        return ItemJpaEntity.builder()
                .name(request.getName())
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public ItemCreateResponse toResponse() {
        return ItemCreateResponse.builder()
                .itemCode(this.itemCode)
                .name(this.name)
                .price(this.price)
                .imageUrl(this.imageUrl)
                .build();
    }

}
