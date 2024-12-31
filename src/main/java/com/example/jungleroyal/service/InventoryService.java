package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.inventory.InventoryListReponse;
import com.example.jungleroyal.repository.ItemJpaEntity;

import java.util.List;

public interface InventoryService {
    InventoryListReponse getItemsByJwt(String jwtToken);

}
