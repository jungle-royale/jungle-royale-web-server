package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {
}
