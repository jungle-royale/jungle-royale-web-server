package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.ItemJpaEntity;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    ItemJpaEntity save(ItemJpaEntity itemJpaEntity);

    Optional<ItemJpaEntity> findById(Long itemId);
    List<ItemJpaEntity> findAll();

}
