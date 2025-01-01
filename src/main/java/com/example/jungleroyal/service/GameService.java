package com.example.jungleroyal.service;

import com.example.jungleroyal.repository.UserJpaEntity;
import com.example.jungleroyal.common.types.UserRole;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    public void participateInGame(UserJpaEntity userJpaEntity){
        if (userJpaEntity.getRole() == UserRole.MEMBER){
            System.out.println("Member user " + userJpaEntity.getUsername() + " has joined the game!");
        } else {
            System.out.println("Guest user " + userJpaEntity.getUsername() + " has joined the game!");
        }
    }
}
