package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.gameroom.GameRoomJpaEntity;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.domain.post.PostUpdateRequest;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.PostJpaEntity;
import com.example.jungleroyal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";
    private final UserServiceImpl userService;
    private final PostRepository postRepository;

    @Override
    public void savePost(PostCreateResponse postCreateResponse, Long userId) {
        MultipartFile file = postCreateResponse.getImage();

        String filePath = null;
        // 파일 저장 로직
        if (file != null && !file.isEmpty()) {
            try {
                // 업로드 디렉토리 확인 및 생성
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 고유 파일명 생성
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                filePath = uploadPath.resolve(fileName).toString();
                file.transferTo(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UserJpaEntity userJpaEntity = userService.getUserJpaEntityById(userId);
        PostJpaEntity postJpaEntity = PostJpaEntity.fromPostCreateResponse(postCreateResponse, filePath, userJpaEntity);
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
    public void deletePost(Long postId) {
        // 게시글 존재 여부 확인
        PostJpaEntity postJpaEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 삭제 처리
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
        String username = post.getUserJpaEntity().getUsername();
        Long userId = post.getUserJpaEntity().getId();
        return post.toPostResponse(username, userId);
    }

}
