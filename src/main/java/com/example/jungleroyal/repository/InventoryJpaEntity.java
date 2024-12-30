package com.example.jungleroyal.repository;

import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    private List<ItemJpaEntity> items = new ArrayList<>();

    public void addItem(ItemJpaEntity item) {
        this.items.add(item);
    }

    public void removeItem(ItemJpaEntity item) {
        this.items.remove(item);
    }
}
