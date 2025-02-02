package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, Long> {
    Optional<InventoryJpaEntity> findByUser(UserJpaEntity user);

    @Query("SELECT i FROM InventoryJpaEntity inv JOIN inv.items i WHERE inv.user.id = :userId")
    List<ItemJpaEntity> findItemsByUserId(@Param("userId") Long userId);
}
