package com.example.jungleroyal.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDto {
    private String content;
    private String sender;

    public ChatMessageDto(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }
}
