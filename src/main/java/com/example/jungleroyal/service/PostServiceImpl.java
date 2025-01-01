package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.domain.post.*;
import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.repository.PostJdbcRepository;
import com.example.jungleroyal.repository.PostJpaEntity;
import com.example.jungleroyal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final UserServiceImpl userService;
    private final PostRepository postRepository;
    private final PostJdbcRepository postJdbcRepository;
    private final JungleFileUtils fileUtils;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";
//    private static final String baseUrl = "http://192.168.1.241:8080/uploads/";

    @Value("${base.url.post}")
    private String baseUrl;

    @Override
    public void savePost(PostCreateResponse postCreateResponse, Long userId) {
        MultipartFile file = postCreateResponse.getImage();

        String filePath = null;

        String newFilePath = fileUtils.handleFileUpload(file, filePath, UPLOAD_DIR);

        UserJpaEntity userJpaEntity = userService.getUserJpaEntityById(userId);
        PostJpaEntity postJpaEntity = PostJpaEntity.fromPostCreateResponse(postCreateResponse, newFilePath, userJpaEntity);
        postRepository.save(postJpaEntity);
    }

    @Transactional
    @Override
    public List<PostDto> getPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostJpaEntity::toDto)
                .toList();
    }


    @Override
    @Transactional
    public void updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        // 게시글 존재 여부 확인
        PostJpaEntity postJpaEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 업데이트 수행
        postJpaEntity.setTitle(postUpdateRequest.getTitle());
        postJpaEntity.setContent(postUpdateRequest.getContent());
        postJpaEntity.setUpdatedAt(LocalDateTime.now());

        // 파일 처리 (별도 메서드 호출)
        String newFilePath = fileUtils.handleFileUpload(postUpdateRequest.getImage(), postJpaEntity.getFilePath(), UPLOAD_DIR);
        postJpaEntity.setFilePath(newFilePath);

        // 엔티티 저장
        postRepository.save(postJpaEntity);

    }

    /**
     * 게시글의 작성자인지 확인
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional
    public boolean isPostOwner(String userId, Long postId) {
        return postRepository.findByIdAndUserJpaEntity_Id(postId, Long.parseLong(userId)).isPresent();
    }

    @Override
    @Transactional
    public void deletePost(Long postId) throws IOException {
        // 게시글 존재 여부 확인
        PostJpaEntity postJpaEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 삭제 처리
        Files.deleteIfExists(Paths.get(postJpaEntity.getFilePath()));
        postRepository.delete(postJpaEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        PostJpaEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 조회수 증가
        post.incrementViews();
        postRepository.save(post); // 변경사항 저장

        String imageUrl = fileUtils.generateImageUrl(post.getFilePath(),baseUrl); // 파일 경로를 URL로 변환
        String username = post.getUserJpaEntity().getUsername();
        Long userId = post.getUserJpaEntity().getId();

        return post.toPostResponse(username, userId, imageUrl);
    }

    @Override
    @Transactional
    public PageResponse<PostListResponse> getPostsByPagination(int page, int limit) {
        List<PostListResponse> posts = postJdbcRepository.findPostsByPagination(page, limit);
        int totalPosts = postJdbcRepository.countTotalPosts();

        return PageResponse.<PostListResponse>builder()
                .data(posts)
                .total(totalPosts)
                .build();
    }

}
