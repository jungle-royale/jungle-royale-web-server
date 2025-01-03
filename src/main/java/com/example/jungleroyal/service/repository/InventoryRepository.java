package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.InventoryJpaEntity;
import com.example.jungleroyal.infrastructure.ItemJpaEntity;
import com.example.jungleroyal.infrastructure.UserJpaEntity;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    List<ItemJpaEntity> findItemsByUserId(long l);

    Optional<InventoryJpaEntity> findByUser(UserJpaEntity user);

    InventoryJpaEntity save(InventoryJpaEntity newInventory);
}
