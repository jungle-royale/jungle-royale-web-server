package com.example.oauthlogin.controller;

import com.example.oauthlogin.domain.OwnerAuth;
import com.example.oauthlogin.service.ManagerLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ManagerLogoutController {
    private final ManagerLogoutService managerLogoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            OwnerAuth owner,
            @RequestHeader("refresh_token") String refreshToken
    ){
        managerLogoutService.logout(owner.getId(), refreshToken);
        return ResponseEntity.noContent().build();
    }
}
