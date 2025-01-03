package com.example.jungleroyal.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlackList {
    private Long id;
    private String invalidRefreshToken;

}
