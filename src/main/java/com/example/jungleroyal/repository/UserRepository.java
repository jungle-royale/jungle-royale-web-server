package com.example.jungleroyal.repository;

import java.util.Optional;

public interface UserRepository {
    Optional<UserJpaEntity> findByKakaoId(String kakaoId);

    Optional<UserJpaEntity> findById(long userId);

    UserJpaEntity save(UserJpaEntity user);

    String findUsernameById(long userId);
}
