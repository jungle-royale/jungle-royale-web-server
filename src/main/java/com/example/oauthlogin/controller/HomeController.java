package com.example.oauthlogin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "home.html";
    }

//    @GetMapping("/auth/kakao/callback")
//    public @ResponseBody String kakaoCallback(String code){
//
//        RestTemplate rt = new RestTemplate();
//
//        // HTTP POST를 요청할 때 보내는 데이터(body)를 설명해주는 헤더도 만들어 같이 보내줘야 한다.
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // body 데이터를 담을 오브젝트인 MultiValueMap를 만들어보자
//        // body는 보통 key, value의 쌍으로 이루어지기 때문에 자바에서 제공해주는 MultiValueMap 타입을 사용한다.
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", "9b5e1f47241e82beb559d44bd2a25377");
//        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
//        params.add("code", code);
//
//        // 요청하기 위해 헤더(Header)와 데이터(Body)를 합친다.
//        // kakaoTokenRequest는 데이터(Body)와 헤더(Header)를 Entity가 된다.
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token", // https://{요청할 서버 주소}
//                HttpMethod.POST, // 요청할 방식
//                kakaoTokenRequest, // 요청할 때 보낼 데이터
//                String.class // 요청 시 반환되는 데이터 타입
//        );
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        OAuthToken oAuthToken = null;
//        try {
//            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        // POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
//        // 이 때 필요한 라이브러리가 RestTemplate, 얘를 쓰면 http 요청을 편하게 할 수 있다.
//        RestTemplate rt2 = new RestTemplate();
//
//        System.out.println(":::::::: 전달받은 엑세스 토큰 oAuthToken = " + oAuthToken.getAccess_token());
//        // HTTP POST를 요청할 때 보내는 데이터(body)를 설명해주는 헤더도 만들어 같이 보내줘야 한다.
//        HttpHeaders headers2 = new HttpHeaders();
//        headers2.add("Authorization", "Bearer "+oAuthToken.getAccess_token());
//        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest= new HttpEntity<>(headers2);
//
//        // POST 방식으로 Http 요청한다. 그리고 response 변수의 응답 받는다.
//        ResponseEntity<String> response2 = rt2.exchange(
//                "https://kapi.kakao.com/v2/user/me", // https://{요청할 서버 주소}
//                HttpMethod.POST, // 요청할 방식
//                kakaoProfileRequest, // 요청할 때 보낼 데이터
//                String.class // 요청 시 반환되는 데이터 타입
//        );
//
//        System.out.println(":::::::: 카카오로부터 전달받은 개인정보 : " + response2.getBody());
//
//        return response2.getBody();
//    }
}
