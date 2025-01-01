package com.example.jungleroyal.domain.post;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private int views;
//    private String imageUrl;
    private LocalDateTime createdAt;
}
