package com.notificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;
    @Column(name = "className")
    private String className;
    @Column(name = "courseName")
    private String courseName;
    @Column(name = "message")
    private String message;
    @Column(name = "sent_notification_at")
    private LocalDateTime notificationSentAt;
    @Column(name = "is_read")
    private boolean isRead;
}
