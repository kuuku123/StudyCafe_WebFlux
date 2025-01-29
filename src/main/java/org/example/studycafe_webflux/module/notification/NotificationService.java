package org.example.studycafe_webflux.module.notification;

import org.example.studycafe_webflux.module.notification.dto.NotificationDto;
import org.example.studycafe_webflux.module.notification.dto.NotificationEvent;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationService {
    private final Sinks.Many<NotificationEvent> sink;

    public NotificationService() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<ServerSentEvent<NotificationEvent>> getNotifications(String email) {
        return sink.asFlux()
                .filter(event -> event.getEmail().equals(email)) // Filter by client
                .map(event -> ServerSentEvent.builder(event) .event(event.getEventName()) // Add the event name
                        .build())
                .mergeWith(
                        Flux.just(
                                ServerSentEvent.<NotificationEvent>builder()
                                        .event("initial")  // or any event name you like
                                        .data(new NotificationEvent(-1L, email, null, "connectionEstablished"))
                                        .build()
                        )
                );
    }


    public void notifyClientsStudyCreate(NotificationDto notificationDto) {
        String eventName = switch (notificationDto.getNotificationType()) {
            case STUDY_CREATED -> "StudyCreated";
            case EVENT_ENROLLMENT -> "EventEnrollment";
            case STUDY_UPDATED -> "StudyUpdated";
        };

        NotificationEvent event = new NotificationEvent(
                notificationDto.getId(),
                notificationDto.getAccountEmail(),
                eventName,
                notificationDto.getStudyPath()
        );

        sink.tryEmitNext(event);
    }
}

