package com.aau.wizard.controller;

import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Neue Verbindung: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Nachricht empfangen: " + message.getPayload());
        session.sendMessage(new TextMessage("Echo vom Server: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Fehler: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Verbindung geschlossen: " + session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
