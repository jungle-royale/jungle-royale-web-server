package com.example.jungleroyal.repository;

import com.example.jungleroyal.domain.user.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface InventoryRepository extends JpaRepository<InventoryJpaEntity, Long> {
    Optional<InventoryJpaEntity> findByUser(UserJpaEntity user);
}
