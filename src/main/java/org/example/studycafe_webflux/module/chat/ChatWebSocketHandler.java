package org.example.studycafe_webflux.module.chat;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatWebSocketHandler implements WebSocketHandler {

    // Map to store a sink per room.
    private final ConcurrentMap<String, Sinks.Many<String>> roomSinks = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Extract the roomId from the URL.
        String roomId = extractRoomId(session.getHandshakeInfo().getUri());

        // Get or create a sink for the room.
        Sinks.Many<String> roomSink = roomSinks.computeIfAbsent(roomId, id ->
                Sinks.many().multicast().onBackpressureBuffer(256, false));

        // Prepare an output flux converting the sink’s messages into WebSocket messages.
        Flux<WebSocketMessage> outputMessages = roomSink.asFlux()
                .map(session::textMessage);

        // Process incoming messages: each message is pushed into the room-specific sink.
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(roomSink::tryEmitNext)
                .then();

        // Send output messages to the client and subscribe to the input flux.
        return session.send(outputMessages).and(input);
    }

    // Utility to extract roomId from the URI using regex.
    private String extractRoomId(URI uri) {
        // Assumes URL pattern like /chat/{roomId}
        Pattern pattern = Pattern.compile("/chat/(\\w+)");
        Matcher matcher = pattern.matcher(uri.getPath());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "default";
    }
}
