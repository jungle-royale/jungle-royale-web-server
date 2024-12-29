package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.gameroom.GameRoomListResponse;
import com.example.jungleroyal.domain.gameroom.GameRoomListWithUserReponse;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostListResponse;
import com.example.jungleroyal.domain.user.UserInfoUsingRoomListResponse;
import com.example.jungleroyal.service.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> listAllRooms() {
        List<PostListResponse> responseList = postService.getPosts()
                .stream()
                .map(PostListResponse::fromDto) // GameRoomDto → GameRoomResponse 변환
                .toList();
        return ResponseEntity.ok().body(responseList);

    }
}
