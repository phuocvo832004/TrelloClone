package com.example.userservice.infrastructure;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "user-login", groupId = "user-service-group")
    public void listenUserLogin(String message) {
        System.out.println("Received login event: " + message);
    }

    @KafkaListener(topics = "user-logout", groupId = "user-service-group")
    public void listenUserLogout(String message) {
        System.out.println("Received logout event: " + message);
    }
}
