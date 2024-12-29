package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.repository.PostJpaEntity;

import java.util.List;

public interface PostService {
    void savePost(PostCreateResponse postCreateResponse, Long userId);
    List<PostDto> getPosts();
}
