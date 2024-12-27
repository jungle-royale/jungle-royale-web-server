package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.dto.MessageDto;
import com.example.jungleroyal.service.RedisPubService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pubsub", description = "Pubsub API")
@RequestMapping("/redis/pubsub")
public class RedisPubSubController {
    private final RedisPubService redisSubscribeService;

    /**
     *
     * @param channel
     * @param message
     */
    @PostMapping("/send")
    public void sendMessage(@RequestParam(required = true) String channel, @RequestBody MessageDto message) {
        log.info("Redis Pub MSG Channel = {}", channel);
        redisSubscribeService.pubMsgChannel(channel, message);
    }

    /**
     *
     * @param channel
     */
    @PostMapping("/cancel")
    public void cancelSubChannel(@RequestParam String channel) {
        redisSubscribeService.cancelSubChannel(channel);
    }
}
