package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.domain.item.ItemUpdateRequest;

public interface ItemService {
    ItemCreateResponse createItem(ItemCreateRequest itemCreateRequest);

    void updatePost(Long itemId, ItemUpdateRequest itemUpdateRequest);
}
