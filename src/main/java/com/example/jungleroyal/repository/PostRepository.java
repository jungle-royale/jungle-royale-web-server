package com.example.jungleroyal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostJpaEntity, Long > {
    Optional<PostJpaEntity> findByIdAndUserJpaEntity_Id(Long postId, Long userId);

}
