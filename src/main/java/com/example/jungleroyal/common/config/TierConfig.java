package com.example.jungleroyal.common.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "tiers")
public class TierConfig {
    private List<Tier> tiers;

    @PostConstruct
    public void init() {
        if (tiers == null || tiers.isEmpty()) {
            System.out.println("❌ Tier configuration is not loaded properly.");
        } else {
            System.out.println("✅ Tier configuration loaded: " + tiers);
        }
    }

    @Data
    public static class Tier {
        private String name;
        private int minScore;
        private int maxScore;
        private String imageUrl;
    }
}
