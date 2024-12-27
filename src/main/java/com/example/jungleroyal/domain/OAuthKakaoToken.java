package com.example.jungleroyal.domain;

import lombok.Data;

@Data
public class OAuthKakaoToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;
    private String id_token; // 추가된 필드
}
