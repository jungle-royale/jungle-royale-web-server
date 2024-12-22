package com.example.oauthlogin.service;

import com.example.oauthlogin.repository.BlackListRepository;
import com.example.oauthlogin.util.AuthTokensGenerator;
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
