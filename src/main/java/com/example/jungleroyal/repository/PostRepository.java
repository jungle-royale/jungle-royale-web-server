package com.example.jungleroyal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostJpaEntity, Long > {
    Optional<PostJpaEntity> findByIdAndUserJpaEntity_Id(Long postId, Long userId);
    @Modifying
    @Query("update PostJpaEntity p set p.views = p.views + 1 where p.id = :postId")
    void incrementViewsByPostId(@Param("postId") Long postId);

}
