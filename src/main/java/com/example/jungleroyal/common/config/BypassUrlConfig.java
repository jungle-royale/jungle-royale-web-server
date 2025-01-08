package com.example.jungleroyal.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt.bypass")
public class BypassUrlConfig {

    private List<String> urls; // YAML의 jwt.bypass.urls 값을 매핑

    public List<String> getBypassUrls() {
        return urls;
    }

    public boolean isBypassUrl(String requestUri) {
        return getBypassUrls().stream()
                .anyMatch(bypassUrl -> requestUri.matches(bypassUrl.replace("**", ".*")));
    }
}
