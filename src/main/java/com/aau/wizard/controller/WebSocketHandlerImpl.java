package com.aau.wizard.controller;

import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {
    /**
     * Called after a new WebSocket connection is established.
     *
     * @param session the WebSocket session representing the connection
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("New Connection: " + session.getId());
    }

    /**
     * Handles an incoming WebSocket message from the client.
     *
     * @param session the session from which the message was received
     * @param message the received WebSocket message
     * @throws Exception if an error occurs while handling the message
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Message received: " + message.getPayload());
        session.sendMessage(new TextMessage("Echo from the server: " + message.getPayload()));
    }

    /**
     * Handles a transport-level error that occurred during communication.
     *
     * @param session   the session where the error occurred
     * @param exception the exception representing the error
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Error: " + exception.getMessage());
    }

    /**
     * Called after a WebSocket connection is closed.
     *
     * @param session the WebSocket session that was closed
     * @param status  the reason/status for the connection closure
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Connection closed: " + session.getId());
    }

    /**
     * Indicates whether this handler supports partial (fragmented) messages.
     *
     * @return false since partial messages are not supported
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
