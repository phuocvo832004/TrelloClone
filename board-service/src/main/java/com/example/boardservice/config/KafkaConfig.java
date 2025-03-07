package com.example.boardservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic boardEventsTopic() {
        return new NewTopic("board-events", 1, (short) 1);
    }
}
