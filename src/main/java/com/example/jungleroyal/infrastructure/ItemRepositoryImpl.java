package com.example.jungleroyal.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository{
    private final ItemJpaRepository itemJpaRepository;
    @Override
    public ItemJpaEntity save(ItemJpaEntity itemJpaEntity) {
        return itemJpaRepository.save(itemJpaEntity);
    }

    @Override
    public Optional<ItemJpaEntity> findById(Long itemId) {
        return itemJpaRepository.findById(itemId);
    }

    @Override
    public List<ItemJpaEntity> findAll() {
        return itemJpaRepository.findAll();

    }
}
