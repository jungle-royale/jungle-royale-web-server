package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.item.ItemJpaEntity;

import java.util.List;

public interface InventoryService {
    List<ItemJpaEntity> getItemsByJwt(String jwtToken);

}
