package com.example.tacademy.recyclerviewtest.model;

/**
 * 채팅 메시지 구조 -> 디비 구조
 */

public class ChatMessage {
    String username;
    String message;

    // Default 생성자 꼭 만든다.
    public ChatMessage() {
    }

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
