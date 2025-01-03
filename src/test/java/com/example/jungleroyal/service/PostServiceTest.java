package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JungleFileUtils;
import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.post.PageResponse;
import com.example.jungleroyal.domain.post.PostDto;
import com.example.jungleroyal.infrastructure.PostJdbcRepository;
import com.example.jungleroyal.domain.post.PostListResponse;
import com.example.jungleroyal.domain.post.PostResponse;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.infrastructure.PostJpaEntity;
import com.example.jungleroyal.infrastructure.PostJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {
    @Mock
    private PostJpaRepository postRepository;

    @Mock
    private PostJdbcRepository postJdbcRepository;

    @Mock
    private JungleFileUtils jungleFileUtils;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void Post_DTO로_변경_시_UTC를_한국시간으로_변경O() {
        // Given: UTC 시간
        LocalDateTime utcNow = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

        // When: DTO 변환 (KST 시간 변환 포함)
        PostDto postDto = PostDto.builder()
                .createdAt(TimeUtils.convertUtcToKst(utcNow))
                .build();

        // Then: 변환된 시간이 예상 시간과 일치하는지 확인
        LocalDateTime kstTime = utcNow.atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
        Assertions.assertEquals(postDto.getCreatedAt(), kstTime);
    }

    @Test
    void 작성자가_본인일_경우_true값을_반환한다() {
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
    void 작성자가_본인이_아닐_경우_false값을_반환한다() {
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
    void 작성자id에_해당하는_게시글이_없을_경우_예외를_발생시킨다() {
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
    void 파일을_저장하고_기존_파일을_삭제한다() throws IOException {
        // given
        MultipartFile newFile = mock(MultipartFile.class);
        String existingFilePath = "src/main/resources/static/uploads/old_file.txt";
        String uploadDir = "src/main/resources/static/uploads/items";

        when(newFile.getOriginalFilename()).thenReturn("new_file.txt");
        when(newFile.isEmpty()).thenReturn(false);

        // 파일이 있는 경로를 가정
        Path mockPath = Paths.get(existingFilePath);
        Files.createFile(mockPath);

        // when
        String result = jungleFileUtils.handleFileUpload(newFile, existingFilePath,uploadDir);

        // then
        assertNotNull(result);
        assertTrue(Files.exists(Paths.get(result)));
        assertFalse(Files.exists(mockPath)); // 기존 파일 삭제 확인
    }

    @Test
    void 게시글_조회시_PostResponse를_정상적으로_반환한다() {
        // given
        Long postId = 1L;
        String filePath = "src/main/resources/static/uploads/testImage.jpg";

        UserJpaEntity user = UserJpaEntity.builder()
                .id(42L)
                .username("testUser")
                .build();

        PostJpaEntity post = PostJpaEntity.builder()
                .id(postId)
                .title("Test Title")
                .content("Test Content")
                .views(100)
                .userJpaEntity(user)
                .filePath(filePath)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        PostResponse response = postService.getPostById(postId);

        // then
        assertNotNull(response, "PostResponse 객체는 null이 아니어야 합니다.");
        assertEquals(post.getId(), response.getId(), "Post ID가 일치해야 합니다.");
        assertEquals(post.getTitle(), response.getTitle(), "제목이 일치해야 합니다.");
        assertEquals(post.getContent(), response.getContent(), "내용이 일치해야 합니다.");
        assertEquals(user.getUsername(), response.getWriter(), "작성자 이름이 일치해야 합니다.");
        assertEquals(user.getId(), response.getWriterId(), "작성자 ID가 일치해야 합니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void 페이지네이션_응답을_정상적으로_반환한다() {
        // given
        int page = 1;
        int pageSize = 5;

        List<PostListResponse> mockPosts = List.of(
                PostListResponse.builder().id(1L).title("Post 1").content("Content 1").username("User 1").views(10).createdAt(LocalDateTime.now()).build(),
                PostListResponse.builder().id(2L).title("Post 2").content("Content 2").username("User 2").views(15).createdAt(LocalDateTime.now()).build()
        );

        when(postJdbcRepository.findPostsByPagination(0, 5)).thenReturn(mockPosts);
        when(postJdbcRepository.countTotalPosts()).thenReturn(50);

        // when
        PageResponse<PostListResponse> response = postService.getPostsByPagination(page, pageSize);

        // then
        assertNotNull(response);
        assertEquals(50L, response.getTotal());
        assertEquals(2, response.getData().size());
        verify(postJdbcRepository, times(1)).findPostsByPagination(0, 5);
        verify(postJdbcRepository, times(1)).countTotalPosts();
    }

    @Test
    void 게시글이_없을_경우_예외를_발생시킨다() {
        // given
        Long postId = 5L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> postService.getPostById(postId));
        assertEquals("해당 게시글을 찾을 수 없습니다.", exception.getMessage(), "예외 메시지가 정확해야 합니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void 게시글_조회시_조회수가_증가한다() {
        // given
        Long postId = 1L;
        UserJpaEntity user = UserJpaEntity.builder().id(42L).username("testUser").build();
        PostJpaEntity post = PostJpaEntity.builder()
                .id(postId)
                .title("Test Title")
                .content("Test Content")
                .views(100)
                .userJpaEntity(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        PostResponse response = postService.getPostById(postId);

        // then
        assertEquals(101, post.getViews(), "조회수가 1 증가해야 합니다.");
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void 게시글_삭제시_파일과_엔티티가_정상적으로_삭제된다() throws IOException {
        // given
        Long postId = 1L;
        String filePath = "src/main/resources/static/uploads/testImage.jpg";

        PostJpaEntity postJpaEntity = PostJpaEntity.builder()
                .id(postId)
                .filePath(filePath)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity));

        // 파일 생성 (가상 파일)
        Path mockPath = Paths.get(filePath);
        Files.createDirectories(mockPath.getParent()); // 디렉토리 생성
        Files.createFile(mockPath); // 파일 생성

        // 파일이 존재하는지 확인
        assertTrue(Files.exists(mockPath), "테스트 파일이 존재해야 합니다.");

        // when
        postService.deletePost(postId);

        // then
        assertFalse(Files.exists(mockPath), "파일이 삭제되어야 합니다.");
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(postJpaEntity);
    }

    @Test
    void 삭제할_게시글이_존재하지_않을_경우_예외를_발생시킨다() {
        // given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> postService.deletePost(postId));
        assertEquals("해당 게시글을 찾을 수 없습니다.", exception.getMessage(), "예외 메시지가 정확해야 합니다.");

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any());
    }
}
