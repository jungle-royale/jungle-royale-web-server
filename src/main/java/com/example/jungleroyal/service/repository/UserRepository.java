package com.example.jungleroyal.service.repository;

import com.example.jungleroyal.infrastructure.UserJpaEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserJpaEntity> findByKakaoId(String kakaoId);

    Optional<UserJpaEntity> findById(long userId);

    UserJpaEntity save(UserJpaEntity user);

    String findUsernameById(long userId);

    void saveAll(List<UserJpaEntity> users);

    List<UserJpaEntity> findAllByClientIds(List<String> clientIds);

    void delete(long userId);

    Optional<UserJpaEntity> findByClientId(String clientId);

    boolean existsByUsername(String nickname);

    List<UserJpaEntity> findAll();
}
