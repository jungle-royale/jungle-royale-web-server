package com.example.jungleroyal.service;

import com.example.jungleroyal.repository.ItemJpaEntity;

import java.util.List;

public interface InventoryService {
    List<ItemJpaEntity> getItemsByJwt(String jwtToken);

}
