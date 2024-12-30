package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.shop.ShopResponse;
import com.example.jungleroyal.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping()
    public ResponseEntity<ShopResponse> getShopPage(@RequestHeader("Authorization") String jwt) {
        ShopResponse response = shopService.getShopPage(jwt);
        return ResponseEntity.ok(response);
    }
}
