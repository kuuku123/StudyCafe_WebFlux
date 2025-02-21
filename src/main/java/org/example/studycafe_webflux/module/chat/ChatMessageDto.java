package org.example.studycafe_webflux.module.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String id;
    private String studyPath;
    private String email;
    private String text;
    private LocalDateTime createdAt;
}
