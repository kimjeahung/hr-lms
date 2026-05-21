package com.hr.backend.domain.notification.dto;

import com.hr.backend.domain.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {

    private Long   notificationId;
    private String type;
    private String message;
    private Long   enrollmentId;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.notificationId = n.getNotificationId();
        this.type           = n.getType().name();
        this.message        = n.getMessage();
        this.enrollmentId   = n.getEnrollmentId();
        this.read           = n.isRead();
        this.createdAt      = n.getCreatedAt();
    }
}
