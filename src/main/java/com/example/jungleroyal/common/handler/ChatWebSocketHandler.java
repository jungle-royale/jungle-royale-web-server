package com.example.jungleroyal.common.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> clientSession = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("[+] afterConnectionEstablished :: " + session.getId());
        clientSession.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("[+] handleTextMessage :: " + session);
        System.out.println("[+] handleTextMessage :: " + message.getPayload());

        clientSession.forEach((key, value) -> {
            if (value.isOpen()) { // 세션이 열려 있는지 확인
                if (!key.equals(session.getId())) {  //같은 아이디가 아니면 메시지를 전달합니다.
                    try {
                        value.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                clientSession.remove(key); // 닫힌 세션은 제거
            }
        });

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        clientSession.remove(session.getId());
        System.out.println("[+] afterConnectionClosed - Session: " + session.getId() + ", CloseStatus: " + status);
    }
}
