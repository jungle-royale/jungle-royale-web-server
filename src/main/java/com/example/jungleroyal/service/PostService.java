package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface PostService {
    void savePost(PostCreateResponse postCreateResponse, Long userId);

    List<PostDto> getPosts();

    void updatePost(Long postId, PostUpdateRequest postUpdateRequest);

    boolean isPostOwner(String userId, Long postId);

    void deletePost(Long postId) throws IOException;

    PostResponse getPostById(Long postId);
    PageResponse<PostListResponse> getPostsByPagination(int page, int limit);

}
