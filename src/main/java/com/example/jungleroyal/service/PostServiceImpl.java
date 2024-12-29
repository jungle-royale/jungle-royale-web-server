package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.gameroom.GameRoomJpaEntity;
import com.example.jungleroyal.domain.post.PostCreateResponse;
import com.example.jungleroyal.domain.post.PostDto;
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
}
