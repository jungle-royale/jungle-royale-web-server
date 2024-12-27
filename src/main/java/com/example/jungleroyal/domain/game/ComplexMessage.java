package com.example.jungleroyal.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplexMessage {
    private String eventId;
    private GameEvent gameEvent;
    private String additionalData;
}
