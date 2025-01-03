package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.post.*;
import com.example.jungleroyal.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/api/posts/create")
    public ResponseEntity<String> create(
            @RequestHeader("Authorization") String authorization,
            PostCreateResponse postCreateResponse
    ) {
        String jwtToken = authorization.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        postService.savePost(postCreateResponse, Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/posts/list")
    public ResponseEntity<PageResponse<PostListResponse>> listAllPosts(
            @RequestParam int page,
            @RequestParam int limit) {
        PageResponse<PostListResponse> response = postService.getPostsByPagination(page,limit);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId
    ){
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> update(
            @PathVariable Long postId,
            PostUpdateRequest postUpdateRequest
    ){
        postService.updatePost(postId, postUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> delete(
            @PathVariable Long postId
    ) throws IOException {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
