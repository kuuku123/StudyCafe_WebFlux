package org.example.studycafe_webflux.infra;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerManager {

    private final KafkaListenerEndpointRegistry registry;

    public KafkaListenerManager(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public KafkaListenerEndpointRegistry getRegistry() {
        return registry;
    }

    public void stopListener() {
        // Stops the listener container completely.
        MessageListenerContainer container = registry.getListenerContainer("notificationListener");
        if (container != null) {
            container.stop();
        }
    }

    public void pauseListener() {
        // Alternatively, you can pause consumption, which might be useful if you plan to resume later.
        MessageListenerContainer container = registry.getListenerContainer("notificationListener");
        if (container != null) {
            container.pause();
        }
    }

    public void resumeListener() {
        MessageListenerContainer container = registry.getListenerContainer("notificationListener");
        if (container != null) {
            container.resume();
        }
    }


}

