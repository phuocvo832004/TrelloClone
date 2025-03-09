package com.example.notificationservice.infrastructure;

import com.example.notificationservice.application.EmailNotificationService;
import com.example.notificationservice.application.PushNotificationService;
import com.example.notificationservice.application.WebSocketNotificationService;
import com.example.notificationservice.domain.Notification;
import com.example.notificationservice.domain.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaNotificationListener {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailNotificationService emailService;
    private final PushNotificationService pushNotificationService;
    private final RestTemplate restTemplate;
    @Value("${user.service.url}")
    private String userServiceUrl;

    private static final Logger logger = LoggerFactory.getLogger(KafkaNotificationListener.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = {"task-events", "board-events"}, groupId = "notification-group")
    public void consumeNotification(ConsumerRecord<String, String> record) {
        String key = record.key();
        String message = record.value();

        // Kiểm tra dữ liệu đầu vào
        if (message == null || message.trim().isEmpty()) {
            logger.error("Received invalid Kafka message - empty or null value: key={}, value={}", key, message);
            return;
        }

        try {
            String userIdStr;
            String notificationMessage;

            // Kiểm tra nếu message là JSON
            if (message.trim().startsWith("{")) {
                JsonNode jsonNode = objectMapper.readTree(message);
                userIdStr = jsonNode.get("key").asText();
                notificationMessage = jsonNode.get("value").asText();
            } else {
                // Trường hợp message không phải JSON, giả định key là userId và value là message
                if (key == null) {
                    logger.error("Key is null and message is not in JSON format: value={}", message);
                    return;
                }
                userIdStr = key;
                notificationMessage = message;
            }

            // Validate và chuyển đổi userId thành UUID
            UUID userId = UUID.fromString(userIdStr);

            // Tạo notification
            Notification notification = Notification.builder()
                    .userId(userId)
                    .message(notificationMessage)
                    .read(false)
                    .build();

            // Lưu và gửi notification
            notificationRepository.save(notification);
            webSocketNotificationService.sendNotification(notification);

            String cacheKey = "notifications:" + userId;
            redisTemplate.opsForList().leftPush(cacheKey, notification);
            redisTemplate.expire(cacheKey, 10, TimeUnit.MINUTES);

            // Gửi email
            String userEmail = getUserEmail(userId);
            if (userEmail != null) {
                emailService.sendEmail(userEmail, "Bạn có thông báo mới!", notificationMessage);
            } else {
                logger.warn("Skipping email notification due to missing email for userId={}", userId);
            }

            // Gửi push notification
            String deviceToken = getUserDeviceToken(userId);
            if (deviceToken != null) {
                pushNotificationService.sendPushNotification(deviceToken, "Bạn có thông báo mới!", notificationMessage);
            } else {
                logger.warn("Skipping push notification due to missing device token for userId={}", userId);
            }

            logger.info("📩 Notification saved: userId={}, message={}", userId, notificationMessage);

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON message: key={}, value={}", key, message, e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format in key: key={}, value={}", key, message, e);
        } catch (Exception e) {
            logger.error("Failed to process Kafka message: key={}, value={}", key, message, e);
        }
    }

    private String getUserEmail(UUID userId) {
        String url = UriComponentsBuilder.fromHttpUrl(userServiceUrl + "/users/" + userId + "/email").toUriString();
        try {
            String email = restTemplate.getForObject(url, String.class);
            if (email == null) {
                logger.warn("No email found for userId={}", userId);
            }
            return email;
        } catch (Exception e) {
            logger.error("Failed to fetch email for userId={}", userId, e);
            return null;
        }
    }

    private String getUserDeviceToken(UUID userId) {
        String url = UriComponentsBuilder.fromHttpUrl(userServiceUrl + "/users/" + userId + "/device-token").toUriString();
        try {
            String token = restTemplate.getForObject(url, String.class);
            if (token == null) {
                logger.warn("No device token found for userId={}", userId);
            }
            return token;
        } catch (Exception e) {
            logger.error("Failed to fetch device token for userId={}", userId, e);
            return null;
        }
    }
}