package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.domain.item.ItemJpaEntity;
import com.example.jungleroyal.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final JungleFileUtils fileUtils;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/items";
    private static final String baseUrl = "http://192.168.1.241:8080/uploads/items/";


    @Override
    public ItemCreateResponse createItem(ItemCreateRequest itemCreateRequest) {
        // 요청을 JPA 엔티티로 변환
        ItemJpaEntity itemJpaEntity = ItemJpaEntity.from(itemCreateRequest);

        MultipartFile file = itemCreateRequest.getImage();

        String filePath = null;

        String newFilePath = fileUtils.handleFileUpload(file, filePath, UPLOAD_DIR);
        itemJpaEntity.setImageUrl(newFilePath);

        ItemJpaEntity savedItem = itemRepository.save(itemJpaEntity);

        String imageUrl = fileUtils.generateImageUrl(savedItem.getImageUrl(),baseUrl);
        savedItem.setImageUrl(imageUrl);
        // 저장된 엔티티를 응답 객체로 변환
        return savedItem.toResponse();
    }
}
