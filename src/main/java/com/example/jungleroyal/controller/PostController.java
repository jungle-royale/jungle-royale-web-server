package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.post.*;
import com.example.jungleroyal.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/create")
    public ResponseEntity<String> create(
            @RequestHeader("Authorization") String authorization,
            PostCreateResponse postCreateResponse
    ) {
        String jwtToken = authorization.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);
        postService.savePost(postCreateResponse, Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<PostListResponse>> listAllPosts(@RequestParam() int page) {
        PageResponse<PostListResponse> response = postService.getPostsByPagination(page);

//        List<PostListResponse> responseList = postService.getPosts()
//                .stream()
//                .map(PostListResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
//                .toList();
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId
    ){
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> update(
            @PathVariable Long postId,
            PostUpdateRequest postUpdateRequest
    ){
        postService.updatePost(postId, postUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> delete(
            @PathVariable Long postId
    ) throws IOException {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
