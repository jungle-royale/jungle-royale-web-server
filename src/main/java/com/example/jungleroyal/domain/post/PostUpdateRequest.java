package com.example.jungleroyal.domain.post;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PostUpdateRequest {
    private String title;
    private String content;
    private MultipartFile image;
}
