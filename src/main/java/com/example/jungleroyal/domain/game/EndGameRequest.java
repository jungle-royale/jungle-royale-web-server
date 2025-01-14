package com.example.jungleroyal.domain.game;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class EndGameRequest {
    private String roomId; //😎 수정 대상
    private List<EndGameUserInfo> users;
}
