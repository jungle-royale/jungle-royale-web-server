package com.example.jungleroyal.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private static final long serialVersionUID = 1L;

    private String message;
    private String sender;
    private String roomId;

}
