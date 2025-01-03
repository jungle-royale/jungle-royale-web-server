package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.CustomUserDetails;
import com.example.jungleroyal.infrastructure.UserJpaEntity;
import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        UserJpaEntity userJpaEntity = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with kakao Id : " + kakaoId));

        return new CustomUserDetails(userJpaEntity.getId(), userJpaEntity.getKakaoId(), userJpaEntity.getUsername());
    }
}
