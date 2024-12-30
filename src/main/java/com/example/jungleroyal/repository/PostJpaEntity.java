package com.example.jungleroyal.repository;

import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static PostJpaEntity fromPostCreateResponse(PostCreateResponse postCreateResponse, String filePath, UserJpaEntity userJpaEntity){
        return PostJpaEntity.builder()
                .userJpaEntity(userJpaEntity)
                .title(postCreateResponse.getTitle())
                .content(postCreateResponse.getContent())
                .views(0)
                .filePath(filePath)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public PostResponse toPostResponse(String writer, Long userId, String imageUrl) {

        return PostResponse.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .writerId(userId)
                .imageUrl(imageUrl)
                .views(this.views)
                .createdAt(this.createdAt)
                .build();
    }
}
