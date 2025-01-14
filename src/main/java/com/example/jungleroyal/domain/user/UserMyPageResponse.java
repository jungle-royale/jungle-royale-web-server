package com.example.jungleroyal.domain.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserMyPageResponse {
    private String username;
    private String gift;
}
