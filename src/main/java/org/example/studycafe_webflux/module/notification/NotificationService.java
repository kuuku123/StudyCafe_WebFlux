package org.example.studycafe_webflux.module.notification;

import java.util.concurrent.atomic.AtomicInteger;
import org.example.studycafe_webflux.infra.KafkaListenerManager;
import org.example.studycafe_webflux.module.notification.dto.NotificationDto;
import org.example.studycafe_webflux.module.notification.dto.NotificationEvent;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationService {

    private final Sinks.Many<NotificationEvent> sink;
    private final AtomicInteger bufferCount = new AtomicInteger(0);
    private final KafkaListenerManager kafkaListenerManager;

    public NotificationService(KafkaListenerManager kafkaListenerManager) {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(
            256, false
        );
        this.kafkaListenerManager = kafkaListenerManager;

    }

    public Flux<ServerSentEvent<NotificationEvent>> getNotifications(String email) {
        // Create a shared Flux that applies the side effect only once per event.
        Flux<NotificationEvent> sharedFlux = sink.asFlux()
            .doOnNext(event -> bufferCount.decrementAndGet())
            .publish()
            .autoConnect(1);

        return sharedFlux
            .filter(event -> event.getEmail().equals(email))
            .map(event -> ServerSentEvent.builder(event)
                .event(event.getEventName())
                .build());
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
        Sinks.EmitResult emitResult = sink.tryEmitNext(event);
        System.out.println("emitResult = " + emitResult.name());
        if (emitResult.isSuccess()) {
            bufferCount.incrementAndGet();
        } else {
            System.out.println("buffer might be full");
            kafkaListenerManager.pauseListener();
        }

    }

    @Scheduled(fixedDelay = 10000)
    public void monitorBufferAndControlListener() {
        int currentBufferCount = bufferCount.get();
        MessageListenerContainer container = kafkaListenerManager.getRegistry()
            .getListenerContainer("notificationListener");
        if (container != null && currentBufferCount < 125 && container.isContainerPaused()) {
            System.out.println(
                "Buffer drained (" + currentBufferCount + "), resuming listener...");
            container.resume();
        }
    }
}

