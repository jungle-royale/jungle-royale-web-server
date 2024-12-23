package com.example.oauthlogin.service;

import com.example.oauthlogin.domain.User;
import com.example.oauthlogin.common.types.UserRole;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    public void participateInGame(User user){
        if (user.getRole() == UserRole.MEMBER){
            System.out.println("Member user " + user.getUsername() + " has joined the game!");
        } else {
            System.out.println("Guest user " + user.getUsername() + " has joined the game!");
        }
    }
}
