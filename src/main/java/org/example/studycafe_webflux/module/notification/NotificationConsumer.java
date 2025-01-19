package org.example.studycafe_webflux.module.notification;


import lombok.RequiredArgsConstructor;
import org.example.studycafe_webflux.module.notification.dto.NotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification_topic", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(NotificationDto notificationDto) {
        System.out.println("Consumed message: " + notificationDto);
        notificationService.notifyClientsStudyCreate(notificationDto);
    }
}
