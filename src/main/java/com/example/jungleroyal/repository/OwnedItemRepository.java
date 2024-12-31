package com.example.jungleroyal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnedItemRepository extends JpaRepository<OwnedItemJpaEntity, Long> {
    @Query("SELECT o FROM OwnedItemJpaEntity o WHERE o.inventory.user.id = :userId")
    List<OwnedItemJpaEntity> findOwnedItemsByUserId(@Param("userId") Long userId);
}
