package com.example.notificationservice.application;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    public void sendPushNotification(String deviceToken, String title, String body) {
        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            System.out.println("Push Notification sent successfully: " + response);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
}
