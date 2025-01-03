package com.example.jungleroyal.infrastructure;

import java.util.List;

public interface OwnedItemRepository {
    List<OwnedItemJpaEntity> findOwnedItemsByUserId(Long id);
    void save(OwnedItemJpaEntity ownedItem);

}
