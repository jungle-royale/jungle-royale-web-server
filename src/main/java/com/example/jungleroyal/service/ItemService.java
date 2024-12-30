package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;

public interface ItemService {
    ItemCreateResponse createItem(ItemCreateRequest itemCreateRequest);
}
