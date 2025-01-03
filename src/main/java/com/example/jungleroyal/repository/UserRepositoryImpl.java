package com.example.jungleroyal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{
    private final UserJpaRepository userJpaRepository;
    @Override
    public Optional<UserJpaEntity> findByKakaoId(String kakaoId) {
        return userJpaRepository.findByKakaoId(kakaoId);
    }

    @Override
    public Optional<UserJpaEntity> findById(long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public UserJpaEntity save(UserJpaEntity user) {
        return userJpaRepository.save(user);
    }

    @Override
    public String findUsernameById(long userId) {
        return userJpaRepository.findUsernameById(userId);
    }
}
