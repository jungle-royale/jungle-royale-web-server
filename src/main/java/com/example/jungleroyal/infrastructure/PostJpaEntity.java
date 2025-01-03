package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.domain.post.PostListResponse;
import com.example.jungleroyal.domain.post.PostResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 유저와 다대일 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래키 컬럼
    private UserJpaEntity userJpaEntity;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int views;

    @Column
    private String filePath; // 파일 경로 또는 URL 저장

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public PostDto toDto() {
        return PostDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .userJpaEntity(userJpaEntity)
                .views(views)
                .username(userJpaEntity.getUsername())
                .filePath(filePath)
                .createdAt(TimeUtils.convertUtcToKst(createdAt)) // KST로 변환
                .updatedAt(TimeUtils.convertUtcToKst(updatedAt)) // KST로 변환
                .build();
    }

    public static PostJpaEntity fromPostCreateResponse(PostCreateResponse postCreateResponse, String filePath, UserJpaEntity userJpaEntity){
        return PostJpaEntity.builder()
                .userJpaEntity(userJpaEntity)
                .title(postCreateResponse.getTitle())
                .content(postCreateResponse.getContent())
                .views(0)
                .filePath(filePath)
                .createdAt(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) // UTC 시간 저장
                .updatedAt(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)) // UTC 시간 저장                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PostResponse toPostResponse(String writer, Long userId, int views) {

        return PostResponse.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .writerId(userId)
                .views(views)
                .createdAt(TimeUtils.convertUtcToKst(this.createdAt))
                .build();
    }

    // toPostListResponse 메서드
    public PostListResponse toPostListResponse() {
        return PostListResponse.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .username(this.userJpaEntity.getUsername()) // 작성자 이름
                .views(this.views)
                .createdAt(this.createdAt)
                .build();
    }

    public void incrementViews() {
        this.views +=1;
    }
}
