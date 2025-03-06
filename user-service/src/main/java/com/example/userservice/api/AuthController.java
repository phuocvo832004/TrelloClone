package com.example.userservice.api;

import com.example.userservice.application.UserService;
import com.example.userservice.domain.User;
import com.example.userservice.infrastructure.JwtUtil;
import com.example.userservice.infrastructure.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final KafkaProducerService kafkaProducerService;


    public AuthController(UserService userService, JwtUtil jwtUtil, KafkaProducerService kafkaProducerService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            String token = jwtUtil.generateToken(user.getUsername());
            kafkaProducerService.sendMessage("user-login", "User " + user.getUsername() + " logged in.");
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        kafkaProducerService.sendMessage("user-logout", "User " + username + " logged out.");
        return ResponseEntity.ok("User logged out successfully");
    }
}
