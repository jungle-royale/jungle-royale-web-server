package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    void savePost(PostCreateResponse postCreateResponse, Long userId);
    List<PostDto> getPosts();

    void updatePost(Long postId, PostUpdateRequest postUpdateRequest);

    boolean isPostOwner(String userId, Long postId);

    void deletePost(Long postId) throws IOException;

    String handleFileUpload(MultipartFile file, String existingFilePath);

    PostResponse getPostById(Long postId);
    PageResponse<PostListResponse> getPostsByPagination(int page);

}
