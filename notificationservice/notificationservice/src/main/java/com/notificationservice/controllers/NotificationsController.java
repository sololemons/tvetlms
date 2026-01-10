package com.notificationservice.controllers;

import com.notificationservice.services.NotificationsService;
import com.shared.dtos.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationsController {
    private final NotificationsService notificationsService;

    @GetMapping("/get/notifications")
    private ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam String className) {
        return ResponseEntity.ok(notificationsService.getAllNotificationsByClassName(className));
    }
    @PostMapping("/mark/notification/read")
    private ResponseEntity<String>markNotificationRead(@RequestParam Integer notificationId) {
        return ResponseEntity.ok(notificationsService.markNotificationsRead(notificationId));
    }
    @DeleteMapping("/delete/notification/{notificationId}")
    private ResponseEntity<String> deleteNotification(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationsService.deleteNotification(notificationId));
    }
}
