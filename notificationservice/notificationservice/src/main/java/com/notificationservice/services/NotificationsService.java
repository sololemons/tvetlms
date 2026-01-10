package com.notificationservice.services;

import com.notificationservice.configuration.RabbitMQConfiguration;
import com.notificationservice.entity.Notifications;
import com.notificationservice.repository.NotificationRepository;
import com.shared.dtos.GetNotificationDto;
import com.shared.dtos.NotificationDto;
import com.shared.dtos.NotificationRequestDto;
import com.shared.dtos.NotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class NotificationsService {
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfiguration.ADD_NOTIFICATIONS)
    private void addNotifications(NotificationRequestDto notificationRequestDto){
        if(notificationRequestDto != null){
            Notifications notifications = new Notifications();
            notifications.setClassName(notificationRequestDto.getClassName());
            notifications.setCourseName(notificationRequestDto.getCourseName());
            notifications.setMessage(notificationRequestDto.getMessage());
            notifications.setRead(false);


            notificationRepository.save(notifications);

        }

    }

    @RabbitListener(queues = RabbitMQConfiguration.GET_NOTIFICATIONS)
    public NotificationsResponse handleGetNotifications(GetNotificationDto request) {

        List<NotificationDto> notifications = notificationRepository.findByClassName(request.getClassName())
                .stream()
                .map(notification -> new NotificationDto(
                        notification.getClassName(),
                        notification.getCourseName(),
                        notification.getNotificationSentAt(),
                        notification.getMessage(),
                        notification.isRead()
                ))
                .toList();

        return new NotificationsResponse(notifications);
    }


    public List<NotificationDto> getAllNotificationsByClassName(String className) {
        return notificationRepository.findByClassName(className)
                .stream()
                .map(notification -> new NotificationDto(
                        notification.getClassName(),
                        notification.getCourseName(),
                        notification.getNotificationSentAt(),
                        notification.getMessage(),
                        notification.isRead()
                ))
                .toList();
    }

    public String markNotificationsRead(Integer notificationId) {
        Notifications notifications = notificationRepository.findById(notificationId).orElseThrow(()
                -> new RuntimeException("Notification not found"));
        notifications.setRead(true);
        notificationRepository.save(notifications);
        return "Notification of Id :" + notificationId + " is read";
    }

    public String deleteNotification(Integer notificationId) {
        Notifications notifications = notificationRepository.findById(notificationId).orElseThrow(()
                -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notifications);
        return "Notification of Id :" + notificationId + " is deleted";

    }
}
