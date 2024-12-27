package com.example.jungleroyal.service;

import com.example.jungleroyal.repository.BlackListRepository;
import com.example.jungleroyal.common.util.AuthTokensGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {
    private final AuthTokensGenerator authTokensGenerator;
    private final BlackListRepository blackListRepository;

    public boolean isBlackListToken(String refreshToken){
        return blackListRepository.existsByInvalidRefreshToken(refreshToken);
    }
}
