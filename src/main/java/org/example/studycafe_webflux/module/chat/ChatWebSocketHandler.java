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

public class ChatWebSocketHandler implements WebSocketHandler {

    // This sink map could be replaced by a more robust room management solution.
    // For demonstration, we'll use a single sink per room id.
    private final Sinks.Many<String> defaultSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Extract the roomId from the URL
        String roomId = extractRoomId(session.getHandshakeInfo().getUri());
        // For simplicity, using the default sink. In a real app, you'd have a map of sinks keyed by roomId.
        Sinks.Many<String> roomSink = defaultSink; // or getSinkForRoom(roomId);

        // Prepare an output flux that converts the sink’s messages into WebSocket messages
        Flux<WebSocketMessage> outputMessages = roomSink.asFlux()
                .map(session::textMessage);

        // Process incoming messages: each message received is pushed into the sink.
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> {
                    // You can add logic to include the roomId with the message if needed.
                    roomSink.tryEmitNext("[" + roomId + "] " + message);
                })
                .then();

        // Send the output messages to the client and subscribe to the input flux.
        return session.send(outputMessages).and(input);
    }

    // Utility to extract roomId from the URI using regex
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
