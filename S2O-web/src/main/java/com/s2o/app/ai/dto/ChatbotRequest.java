package com.s2o.app.ai.dto;

public class ChatbotRequest {
    private String question;
    private Long restaurantId;

    public ChatbotRequest() {}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
