package com.example.s2o_mobile.data.model;

public class ChatMessage {

    private String id;
    private String userId;
    private String message;
    private String reply;
    private long createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String userId, String message, String reply, long createdAt) {
        this.userId = userId;
        this.message = message;
        this.reply = reply;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
