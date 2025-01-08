package com.example.jungleroyal.common.config;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BypassUrlConfig {
    public List<String> getBypassUrls() {
        return List.of(
                "/api/game/**",
                "/api/posts/list"
        );
    }

    public boolean isBypassUrl(String requestUri) {
        return getBypassUrls().stream()
                .anyMatch(bypassUrl -> requestUri.matches(bypassUrl.replace("**", ".*")));
    }
}
