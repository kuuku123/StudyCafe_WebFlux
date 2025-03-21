package org.example.studycafe_webflux.module.notification;

import org.example.studycafe_webflux.module.notification.dto.NotificationEvent;
import org.example.studycafe_webflux.util.MyConstants;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> streamNotifications(
        @RequestHeader(MyConstants.HEADER_USER_EMAIL) String email) {
        return notificationService.getNotifications(email);
    }


}
