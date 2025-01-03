package com.example.jungleroyal.repository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    List<ItemJpaEntity> findItemsByUserId(long l);

    Optional<InventoryJpaEntity> findByUser(UserJpaEntity user);

    InventoryJpaEntity save(InventoryJpaEntity newInventory);
}
