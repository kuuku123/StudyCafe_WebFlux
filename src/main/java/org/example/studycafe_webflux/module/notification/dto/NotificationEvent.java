package org.example.studycafe_webflux.module.notification.dto;

public class NotificationEvent {
    private final String email;
    private final String eventName;
    private final String data;

    public NotificationEvent(String email, String eventName, String data) {
        this.email = email;
        this.eventName = eventName;
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public String getEventName() {
        return eventName;
    }

    public String getData() {
        return data;
    }
}

