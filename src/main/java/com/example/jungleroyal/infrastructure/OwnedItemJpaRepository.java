package com.example.jungleroyal.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OwnedItemJpaRepository extends JpaRepository<OwnedItemJpaEntity, Long> {
    @Query("SELECT o FROM OwnedItemJpaEntity o WHERE o.inventory.user.id = :userId")
    List<OwnedItemJpaEntity> findOwnedItemsByUserId(@Param("userId") Long userId);
}
