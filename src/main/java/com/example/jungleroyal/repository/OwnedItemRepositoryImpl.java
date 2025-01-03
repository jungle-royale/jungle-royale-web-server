package com.example.jungleroyal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OwnedItemRepositoryImpl implements OwnedItemRepository{
    private final OwnedItemJpaRepository ownedItemJpaRepository;

    @Override
    public List<OwnedItemJpaEntity> findOwnedItemsByUserId(Long id) {
        return ownedItemJpaRepository.findOwnedItemsByUserId(id);
    }

    @Override
    public void save(OwnedItemJpaEntity ownedItem) {
        ownedItemJpaRepository.save(ownedItem);
    }
}