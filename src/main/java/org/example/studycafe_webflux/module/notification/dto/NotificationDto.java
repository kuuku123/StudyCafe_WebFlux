package org.example.studycafe_webflux.module.notification.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class NotificationDto {
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    private String accountEmail;

    private String studyPath;

    private LocalDateTime createdDateTime;

    private NotificationType notificationType;
}

