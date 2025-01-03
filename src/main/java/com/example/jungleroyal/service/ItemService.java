package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.domain.item.ItemCreateRequest;
import com.example.jungleroyal.domain.item.ItemCreateResponse;
import com.example.jungleroyal.repository.ItemJpaEntity;
import com.example.jungleroyal.domain.item.ItemUpdateRequest;
import com.example.jungleroyal.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final JungleFileUtils fileUtils;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/items";

    @Value("${base.url.item}")
    private String baseUrl;

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

    public void updateItem(Long itemId, ItemUpdateRequest itemUpdateRequest) {
        // 게시글 존재 여부 확인
        ItemJpaEntity itemJpaEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));

        // 이름 업데이트 (입력 값이 null이 아닌 경우)
        if (itemUpdateRequest.getName() != null && !itemUpdateRequest.getName().isEmpty()) {
            itemJpaEntity.setName(itemUpdateRequest.getName());
        }

        // 가격 업데이트 (입력 값이 null이 아닌 경우)
        if (itemUpdateRequest.getPrice() != null) {
            itemJpaEntity.setPrice(itemUpdateRequest.getPrice());
        }

        // 파일 업데이트 (입력된 파일이 있는 경우만 처리)
        if (itemUpdateRequest.getImage() != null && !itemUpdateRequest.getImage().isEmpty()) {
            String newFilePath = fileUtils.handleFileUpload(
                    itemUpdateRequest.getImage(),
                    itemJpaEntity.getImageUrl(),
                    UPLOAD_DIR
            );
            itemJpaEntity.setImageUrl(newFilePath);
        }

        // 엔티티 저장
        itemRepository.save(itemJpaEntity);
    }
}
