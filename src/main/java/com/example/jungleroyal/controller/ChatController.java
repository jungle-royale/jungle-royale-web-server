package com.example.jungleroyal.controller;

import com.example.jungleroyal.domain.dto.ChatMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class ChatController {
    private final SimpMessagingTemplate template;

    @Autowired
    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/messages")
    public void send2(@RequestBody ChatMessageDto chatMessageDto) {
        template.convertAndSend("/sub/message", chatMessageDto);       // 구독중인 모든 사용자에게 메시지를 전달합니다.
    }
}
