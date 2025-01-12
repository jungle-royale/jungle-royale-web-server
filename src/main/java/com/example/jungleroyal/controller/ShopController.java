package com.example.jungleroyal.controller;

import com.example.jungleroyal.common.util.JwtTokenProvider;
import com.example.jungleroyal.domain.shop.ShopPageResponse;
import com.example.jungleroyal.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     *  상점 페이지 조회
     * @param jwt
     * @return ShopPageResponse
     */
    @GetMapping("/api/shops/items")
    public ResponseEntity<ShopPageResponse> getShopPage(@RequestHeader("Authorization") String jwt) {
        ShopPageResponse response = shopService.getShopPage(jwt);
        return ResponseEntity.ok(response);
    }

    /**
     * 상점 아이템 구매
     * @param jwt
     * @param itemCode
     * @return ShopPageResponse
     */
    @PostMapping("/api/shops/purchase")
    public ResponseEntity<ShopPageResponse> purchaseItem(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long itemCode) {

        // JWT에서 유저 ID 추출
        String jwtToken = jwt.substring(7);
        String userId = jwtTokenProvider.extractSubject(jwtToken);

        shopService.purchaseItem(Long.parseLong(userId), itemCode);

        ShopPageResponse response = shopService.getShopPage(jwt);
        return ResponseEntity.ok(response);
    }
}
