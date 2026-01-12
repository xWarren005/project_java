package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.ChatMessage;
import com.example.s2o_mobile.data.source.remote.ChatbotApi;
import com.example.s2o_mobile.data.source.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class ChatbotRepository {

    private final ChatbotApi chatbotApi;

    public ChatbotRepository() {
        chatbotApi = RetrofitClient.getInstance().create(ChatbotApi.class);
    }

    public Call<ChatMessage> sendMessage(String message) {
        return chatbotApi.sendMessage(message);
    }

    public Call<List<ChatMessage>> getConversation(String userId) {
        return chatbotApi.getConversation(userId);
    }
}
