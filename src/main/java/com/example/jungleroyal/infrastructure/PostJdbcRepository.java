package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.common.util.TimeUtils;
import com.example.jungleroyal.domain.post.PostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<PostListResponse> findPostsByPagination (int page, int limit){
        // 페이지 데이터 조회
        String query = "SELECT p.id, p.title, p.content, u.username, p.views, p.created_at " +
                "FROM posts p " +
                "JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * limit; // 페이지 시작점 계산

        return jdbcTemplate.query(query, new Object[]{limit, offset}, postRowMapper());

    }

    // 전체 게시글 수 조회
    public int countTotalPosts() {
        String query = "SELECT COUNT(*) FROM posts";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

    // RowMapper를 이용한 PostListResponse 변환
    private RowMapper<PostListResponse> postRowMapper() {
        return (rs, rowNum) -> PostListResponse.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                .username(rs.getString("username")) // 작성자 이름
                .views(rs.getInt("views"))
                .createdAt(TimeUtils.convertUtcToKst(rs.getTimestamp("created_at").toLocalDateTime())) // 간단 변환
                .build();
    }
}
