package com.example.notificationservice.application;

import com.example.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notification notification) {
        String destination = "/topic/notifications/" + notification.getUserId();
        messagingTemplate.convertAndSend(destination, notification);
    }
}
