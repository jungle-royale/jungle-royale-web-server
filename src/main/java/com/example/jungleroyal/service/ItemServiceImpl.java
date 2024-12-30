package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.domain.item.ItemJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;

    @Override
    public ItemCreateResponse createItem(ItemCreateRequest itemCreateRequest) {
        // 요청을 JPA 엔티티로 변환
        ItemJpaEntity itemJpaEntity = ItemJpaEntity.from(itemCreateRequest);

        // 데이터베이스에 저장
        ItemJpaEntity savedItem = itemRepository.save(itemJpaEntity);

        // 저장된 엔티티를 응답 객체로 변환
        return savedItem.toResponse();
    }
}
