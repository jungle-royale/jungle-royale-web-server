package com.example.jungleroyal.domain.post;

import com.example.jungleroyal.domain.gameroom.GameRoomDto;
import com.example.jungleroyal.domain.gameroom.GameRoomListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostListResponse {
    private Long id;
    private String title;
    private String content;
    private String username;
    private int views;
    private LocalDateTime createdAt;

    public static PostListResponse fromDto(PostDto postDto) {
        return PostListResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .views(postDto.getViews())
                .createdAt(postDto.getCreatedAt())
                .username(postDto.getUsername())
                .build();
    }
}
