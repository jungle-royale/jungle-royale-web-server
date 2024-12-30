package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.PostJpaEntity;
import com.example.jungleroyal.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;


    @Test
    void isPostOwner_ShouldReturnTrue_WhenUserIsOwner() {
        // given
        String userId = "1";
        Long postId = 1L;

        UserJpaEntity userJpaEntity = UserJpaEntity.builder().id(1L).build();
        PostJpaEntity postJpaEntity = PostJpaEntity.builder().id(postId).userJpaEntity(userJpaEntity).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity));

        // when
        boolean result = postService.isPostOwner(userId, postId);

        // then
        assertTrue(result);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void isPostOwner_ShouldReturnFalse_WhenUserIsNotOwner() {
        // given
        String userId = "2";
        Long postId = 1L;

        UserJpaEntity userJpaEntity = UserJpaEntity.builder().id(1L).build();
        PostJpaEntity postJpaEntity = PostJpaEntity.builder().id(postId).userJpaEntity(userJpaEntity).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity));

        // when
        boolean result = postService.isPostOwner(userId, postId);

        // then
        assertFalse(result);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void isPostOwner_ShouldThrowException_WhenPostNotFound() {
        // given
        String userId = "1";
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> postService.isPostOwner(userId, postId));
        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void handleFileUpload_ShouldSaveNewFileAndDeleteExistingFile() throws IOException {
        // given
        MultipartFile newFile = mock(MultipartFile.class);
        String existingFilePath = "src/main/resources/static/uploads/old_file.txt";

        when(newFile.getOriginalFilename()).thenReturn("new_file.txt");
        when(newFile.isEmpty()).thenReturn(false);

        // 파일이 있는 경로를 가정
        Path mockPath = Paths.get(existingFilePath);
        Files.createFile(mockPath);

        // when
        String result = postService.handleFileUpload(newFile, existingFilePath);

        // then
        assertNotNull(result);
        assertTrue(Files.exists(Paths.get(result)));
        assertFalse(Files.exists(mockPath)); // 기존 파일 삭제 확인
    }

    @Test
    void toPostResponse_ShouldReturnPostResponse_WhenCalled() {
        // given
        UserJpaEntity user = UserJpaEntity.builder().id(1L).username("testUser").build();
        PostJpaEntity post = PostJpaEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .views(100)
                .userJpaEntity(user)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        PostResponse response = post.toPostResponse(user.getUsername(), user.getId());

        // then
        assertNotNull(response);
        assertEquals(post.getId(), response.getId());
        assertEquals(post.getTitle(), response.getTitle());
        assertEquals(post.getUserJpaEntity().getUsername(), response.getWriter());
    }
}
