package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    @Override
    public void save(PostJpaEntity postJpaEntity) {
        postJpaRepository.save(postJpaEntity);
    }

    @Override
    public List<PostJpaEntity> findAll() {
        return postJpaRepository.findAll();
    }

    @Override
    public Optional<PostJpaEntity> findById(Long postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public Optional<PostJpaEntity> findByIdAndUserJpaEntity_Id(Long postId, long userId) {
        return postJpaRepository.findByIdAndUserJpaEntity_Id(postId, userId);
    }

    @Override
    public void delete(PostJpaEntity postJpaEntity) {
        postJpaRepository.delete(postJpaEntity);

    }

    @Override
    @Transactional
    public void incrementViewsByPostId(Long postId) {
        postJpaRepository.incrementViewsByPostId(postId);
    }
}
