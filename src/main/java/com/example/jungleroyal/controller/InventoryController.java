package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventorys")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping("/items")
    public ResponseEntity<List<ItemJpaEntity>> getItems(@RequestHeader("Authorization") String jwt) {
        List<ItemJpaEntity> items = inventoryService.getItemsByJwt(jwt);
        return ResponseEntity.ok(items);
    }
}
