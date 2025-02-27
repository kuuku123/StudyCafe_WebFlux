package org.example.studycafe_webflux.module.chat;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChatMessageDto {

    private String id;
    private String studyPath;
    private String nickname;
    private String email;
    private String text;
    private LocalDateTime createdAt;
}
