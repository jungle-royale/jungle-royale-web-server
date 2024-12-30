package com.example.jungleroyal.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final int PAGESIZE = 5;

    public List<PostListResponse> findPostsByPagination (int page){
        String query =
                "SELECT id, title, content, username, views, created_at " +
                "FROM posts " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        int offset = (page - 1) * PAGESIZE;

        return jdbcTemplate.query(query, new Object[]{PAGESIZE, offset}, (rs, rowNum) ->
            PostListResponse.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .content(rs.getString("content"))
                    .username(rs.getString("username"))
                    .views(rs.getInt("views"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build()
        );
    }
}
