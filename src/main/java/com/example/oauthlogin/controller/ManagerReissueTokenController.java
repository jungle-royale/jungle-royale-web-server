package com.example.oauthlogin.controller;

import com.example.oauthlogin.auth.AuthTokensResponse;
import com.example.oauthlogin.service.ManagerReissueTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/auth")
public class ManagerReissueTokenController {
    private final ManagerReissueTokenService managerReissueTokenService;

    @GetMapping("/reissue-token")
    public ResponseEntity<AuthTokensResponse> reissueToken(
            @RequestHeader("Refresh") String refreshToken
    ) {
        return ResponseEntity.ok(
                managerReissueTokenService.reissueToken(refreshToken)
        );
    }
}
