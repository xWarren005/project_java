package com.example.s2o_mobile.data.model;

public class ChatMessage {
    private String question;
    private String answer;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String question, String answer, long timestamp) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}