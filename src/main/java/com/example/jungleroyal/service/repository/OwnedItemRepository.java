package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.OwnedItemJpaEntity;

import java.util.List;

public interface OwnedItemRepository {
    List<OwnedItemJpaEntity> findOwnedItemsByUserId(Long id);
    void save(OwnedItemJpaEntity ownedItem);

}
