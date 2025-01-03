package com.example.jungleroyal.infrastructure;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    void save(PostJpaEntity postJpaEntity);

    List<PostJpaEntity> findAll();

    Optional<PostJpaEntity> findById(Long postId);

    Optional<PostJpaEntity> findByIdAndUserJpaEntity_Id(Long postId, long l);

    void delete(PostJpaEntity postJpaEntity);

    void incrementViewsByPostId(Long postId);
}
