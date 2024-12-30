package com.example.jungleroyal.domain.post;

import lombok.Builder;
import lombok.Data;

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
    private LocalDateTime createdAt;
}
