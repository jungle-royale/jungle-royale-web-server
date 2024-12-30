package com.example.jungleroyal.service;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.shop.ShopResponse;
import com.example.jungleroyal.domain.user.UserJpaEntity;
import com.example.jungleroyal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService{
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShopResponse getShopPage(String jwt) {
        // JWT에서 유저 ID 추출
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        // 유저 정보 조회
        UserJpaEntity userJpaEntity = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ShopResponse 생성 및 반환
        return ShopResponse.fromUserJpaEntity(userJpaEntity);
    }
}
