package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostListResponse;
import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.domain.post.PostUpdateRequest;
import com.example.jungleroyal.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> listAllPosts() {
        List<PostListResponse> responseList = postService.getPosts()
                .stream()
                .map(PostListResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
                .toList();
        return ResponseEntity.ok().body(responseList);

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
    ){
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
