package com.example.jungleroyal.service;

import com.example.jungleroyal.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {
    private final BlackListRepository blackListRepository;

    public boolean isBlackListToken(String refreshToken){
        return blackListRepository.existsByInvalidRefreshToken(refreshToken);
    }
}
