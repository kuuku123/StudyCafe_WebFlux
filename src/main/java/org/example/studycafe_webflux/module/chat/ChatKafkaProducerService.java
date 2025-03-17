package org.example.studycafe_webflux.module.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaProducerService {

    @Value("${kafka.topic.chat}")
    private String topic;
    private final KafkaTemplate<String, ChatMessageDto> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String jsonMessage) {
        // Optionally, deserialize to validate and/or modify before sending.
        try {
            ChatMessageDto message = objectMapper.readValue(jsonMessage, ChatMessageDto.class);
            // You can also reserialize if needed or send the validated jsonMessage directly.
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            // Handle parsing error
            e.printStackTrace();
        }
    }
}
