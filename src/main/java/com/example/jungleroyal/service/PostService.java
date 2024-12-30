package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.domain.post.PostUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void savePost(PostCreateResponse postCreateResponse, Long userId);
    List<PostDto> getPosts();

    void updatePost(Long postId, PostUpdateRequest postUpdateRequest);

    boolean isPostOwner(String userId, Long postId);

    void deletePost(Long postId);

    String handleFileUpload(MultipartFile file, String existingFilePath);

    PostResponse getPostById(Long postId);
}
