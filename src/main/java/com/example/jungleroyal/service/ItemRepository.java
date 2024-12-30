package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.item.ItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemJpaEntity, Long> {
}
