package com.example.jungleroyal.domain.post;

import com.example.jungleroyal.domain.user.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long id;
    private UserJpaEntity userJpaEntity;
    private String title;
    private String content;
    private String username;
    private int views;
    private String filePath; // 파일 경로 또는 URL 저장
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
