package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.domain.item.ItemUpdateRequest;
import com.example.jungleroyal.domain.post.PostUpdateRequest;
import com.example.jungleroyal.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/api/items/create")
    public ResponseEntity<ItemCreateResponse> createItem(ItemCreateRequest request) {
        ItemCreateResponse createdItem = itemService.createItem(request);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping({"/api/items/{itemId}"})
    public ResponseEntity<String> updateItem(
            @PathVariable Long itemId,
            ItemUpdateRequest itemUpdateRequest
    ){
        itemService.updateItem(itemId, itemUpdateRequest);
        return ResponseEntity.ok().build();
    }

}
