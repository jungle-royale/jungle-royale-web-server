package com.example.jungleroyal.infrastructure;

import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
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

    @Override
    public void saveAll(List<UserJpaEntity> users) {
        userJpaRepository.saveAll(users);
    }

    @Override
    public List<UserJpaEntity> findAllByClientIds(List<String> clientIds) {
        return userJpaRepository.findAllByClientIdIn(clientIds);
    }

    @Override
    public void delete(long userId) {
        userJpaRepository.deleteById(userId);
    }

    @Override
    public Optional<UserJpaEntity> findByClientId(String clientId) {
        return userJpaRepository.findByClientId(clientId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public List<UserJpaEntity> findAll() {
        return userJpaRepository.findAll();
    }
}
