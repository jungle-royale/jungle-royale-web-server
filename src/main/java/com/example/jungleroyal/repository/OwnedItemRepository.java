package com.example.jungleroyal.repository;

import java.util.Arrays;
import java.util.List;

public interface OwnedItemRepository {
    List<OwnedItemJpaEntity> findOwnedItemsByUserId(Long id);
    void save(OwnedItemJpaEntity ownedItem);

}
