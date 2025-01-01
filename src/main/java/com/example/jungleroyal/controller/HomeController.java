package com.example.jungleroyal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @GetMapping
    String home(Model model){
        // 모델에 데이터 추가
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);

        return "home";
    }
}
