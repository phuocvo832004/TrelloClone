package com.example.notificationservice.application;

import com.example.notificationservice.domain.DeviceToken;
import com.example.notificationservice.domain.Notification;
import com.example.notificationservice.domain.NotificationRepository;
import com.example.notificationservice.infrastructure.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DeviceTokenRepository deviceTokenRepository;

    @Cacheable(value = "notifications", key = "#userId")
    public List<Notification> getUserNotifications(UUID userId) {
        String cacheKey = "notifications:" + userId;

        List<Object> cachedNotifications = redisTemplate.opsForList().range(cacheKey, 0, -1);
        if (cachedNotifications != null && !cachedNotifications.isEmpty()) {
            return cachedNotifications.stream()
                    .map(obj -> (Notification) obj)
                    .collect(Collectors.toList());
        }

        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void markAllNotificationsAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void saveDeviceToken(UUID userId, String token) {
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setUserId(userId);
        deviceToken.setToken(token);
        deviceTokenRepository.save(deviceToken);
    }

    public List<String> getUserDeviceTokens(UUID userId) {
        return deviceTokenRepository.findByUserId(userId)
                .stream()
                .map(DeviceToken::getToken)
                .toList();
    }
}
