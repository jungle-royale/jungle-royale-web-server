package com.example.jungleroyal.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "owned_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnedItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownedItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryJpaEntity inventory; // 해당 아이템이 소속된 인벤토리

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private Long templateItemCode; // 원래의 ShopItem 코드 (참고용)
}
