package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.*;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.PostJpaEntity;
import com.example.jungleroyal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";
    private final UserServiceImpl userService;
    private final PostRepository postRepository;
    private final PostJdbcRepository postJdbcRepository;

    @Override
    public void savePost(PostCreateResponse postCreateResponse, Long userId) {
        MultipartFile file = postCreateResponse.getImage();

        String filePath = null;

        String newFilePath = handleFileUpload(file, filePath);

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
        String newFilePath = handleFileUpload(postUpdateRequest.getImage(), postJpaEntity.getFilePath());
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
    public String handleFileUpload(MultipartFile file, String existingFilePath) {
        if (file == null || file.isEmpty()) {
            return existingFilePath; // 파일이 없으면 기존 경로 유지
        }

        try {
            // 업로드 디렉토리 확인 및 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유 파일명 생성 및 저장
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = uploadPath.resolve(fileName).toString();
            file.transferTo(Paths.get(filePath));

            // 기존 파일 삭제 (선택)
            if (existingFilePath != null) {
                Files.deleteIfExists(Paths.get(existingFilePath));
            }

            return filePath; // 새로운 파일 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        PostJpaEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 조회수 증가
        post.incrementViews();
        postRepository.save(post); // 변경사항 저장

        String imageUrl = generateImageUrl(post.getFilePath()); // 파일 경로를 URL로 변환
        String username = post.getUserJpaEntity().getUsername();
        Long userId = post.getUserJpaEntity().getId();

        return post.toPostResponse(username, userId, imageUrl);
    }

    @Override
    @Transactional
    public PageResponse<PostListResponse> getPostsByPagination(int page) {
        // JPA Pageable 사용
        Pageable pageable = PageRequest.of(page - 1, 10); // 페이지는 0부터 시작
        Page<PostJpaEntity> postsPage = postRepository.findAll(pageable);

        // 페이지 데이터 변환
        List<PostListResponse> postList = postsPage.getContent().stream()
                .map(PostJpaEntity::toPostListResponse)
                .toList();

        return PageResponse.<PostListResponse>builder()
                .data(postList)
                .total(postsPage.getTotalElements())
                .build();
    }

    private String generateImageUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null; // 파일 경로가 없으면 null 반환
        }

        String baseUrl = "http://192.168.1.241:8080/uploads/"; // base URL 설정
        return baseUrl + Paths.get(filePath).getFileName().toString(); // 파일명만 URL에 포함
    }

}
