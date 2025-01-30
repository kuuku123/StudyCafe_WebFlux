package org.example.studycafe_webflux.module.notification.dto;

public class NotificationEvent {
    private final Long id;
    private final String email;
    private final String eventName;
    private final String studyPath;

    public NotificationEvent(Long id, String email, String eventName, String path) {
        this.id = id;
        this.email = email;
        this.eventName = eventName;
        this.studyPath = path;
    }


    public String getEmail() {
        return email;
    }

    public String getEventName() {
        return eventName;
    }

    public Long getId() {
        return id;
    }

    public String getStudyPath() {
        return studyPath;
    }
}

