package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/create")
    public ResponseEntity<ItemCreateResponse> createItem(@RequestBody ItemCreateRequest request) {
        ItemCreateResponse createdItem = itemService.createItem(request);
        return ResponseEntity.ok(createdItem);
    }

}
