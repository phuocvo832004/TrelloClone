package com.example.notificationservice.api;

import com.example.notificationservice.application.NotificationService;
import com.example.notificationservice.application.PushNotificationService;
import com.example.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final PushNotificationService pushNotificationService;

    // Lấy danh sách thông báo của user (không phân trang)
    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable UUID userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Đánh dấu một thông báo là đã đọc
    @PatchMapping("/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
    }

    // Lấy danh sách thông báo của user (có phân trang)
    @GetMapping("/paged")
    public Page<Notification> getNotifications(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationService.getUserNotifications(userId, pageable);
    }

    // Đánh dấu tất cả thông báo của user là đã đọc
    @PostMapping("/mark-read")
    public void markAllAsRead(@RequestParam UUID userId) {
        notificationService.markAllNotificationsAsRead(userId);
    }

    @PostMapping("/push")
    public ResponseEntity<String> sendPushNotification(@RequestParam String deviceToken,
                                                       @RequestParam String title,
                                                       @RequestParam String message) {
        pushNotificationService.sendPushNotification(deviceToken, title, message);
        return ResponseEntity.ok("Push Notification Sent Successfully!");
    }

    @PostMapping("/save-token")
    public ResponseEntity<String> saveUserDeviceToken(@RequestParam UUID userId, @RequestParam String deviceToken) {
        notificationService.saveDeviceToken(userId, deviceToken);
        return ResponseEntity.ok("Device Token Saved!");
    }
}