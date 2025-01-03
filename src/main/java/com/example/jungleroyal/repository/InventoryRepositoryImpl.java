package com.example.jungleroyal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository{
    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public List<ItemJpaEntity> findItemsByUserId(long userId) {
        return inventoryJpaRepository.findItemsByUserId(userId);
    }

    @Override
    public Optional<InventoryJpaEntity> findByUser(UserJpaEntity user) {
        return inventoryJpaRepository.findByUser(user);
    }

    @Override
    public InventoryJpaEntity save(InventoryJpaEntity newInventory) {
        return inventoryJpaRepository.save(newInventory);
    }
}
