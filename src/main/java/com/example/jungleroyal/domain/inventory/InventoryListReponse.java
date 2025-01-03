package com.example.jungleroyal.domain.inventory;

import com.example.jungleroyal.infrastructure.ItemJpaEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InventoryListReponse {
    List<ItemJpaEntity> items;
}
