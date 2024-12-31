package com.example.jungleroyal.domain.item;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ItemUpdateRequest {
    private String name;
    private Integer price;
    private MultipartFile image;
}
