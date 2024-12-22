package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.BlackList;
import com.example.oauthlogin.repository.BlackListRepository;
import com.example.oauthlogin.util.RefreshTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ManagerLogoutService {
    private final BlackListRepository blackListRepository;
    private final RefreshTokenValidator refreshTokenValidator;

    public void logout(Long id, String refreshToken){
        refreshTokenValidator.validateToken(refreshToken);
        refreshTokenValidator.validateTokenOwnerId(refreshToken, id);
        refreshTokenValidator.validateLogoutToken(refreshToken);
        blackListRepository.save(new BlackList(refreshToken));
    }
}
