package com.example.oauthlogin.service;

import com.example.oauthlogin.common.util.CustomUserDetails;
import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.repository.UserRepository;
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
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with kakao Id : " + kakaoId));

        return new CustomUserDetails(user.getId(), user.getKakaoId(), user.getUsername());
    }
}
