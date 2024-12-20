package com.example.oauthlogin.controller;


import com.example.oauthlogin.domain.UserDto;
import com.example.oauthlogin.domain.UserJpaEntity;
import com.example.oauthlogin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserJpaEntity user) {
        System.out.println("user = " + user.toString());
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserJpaEntity> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
