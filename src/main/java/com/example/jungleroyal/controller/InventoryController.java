package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.inventory.InventoryListReponse;
import com.example.jungleroyal.repository.ItemJpaEntity;
import com.example.jungleroyal.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping("/api/inventorys/items")
    public ResponseEntity<InventoryListReponse> getItems(@RequestHeader("Authorization") String jwt) {
        InventoryListReponse items = inventoryService.getItemsByJwt(jwt);
        return ResponseEntity.ok(items);
    }
}