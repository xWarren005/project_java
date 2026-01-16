package com.example.s2o_mobile.data.repository;

import com.example.s2o_mobile.data.model.ChatMessage;
import com.example.s2o_mobile.data.source.remote.ApiService;
import com.example.s2o_mobile.data.source.remote.ChatbotApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotRepository {

    private final ChatbotApi chatbotApi;

    public ChatbotRepository() {
        this.chatbotApi = ApiService.getInstance().getChatbotApi();
    }

    public void ask(String question, RepositoryCallback<ChatMessage> callback) {
        if (callback == null) return;

        if (question == null || question.trim().isEmpty()) {
            callback.onError("Question is empty");
            return;
        }

        if (chatbotApi == null) {
            callback.onError("Chatbot API not available");
            return;
        }

        ChatMessage request = new ChatMessage();
        request.setQuestion(question.trim());
        request.setTimestamp(System.currentTimeMillis());

        Call<ChatMessage> call = chatbotApi.ask(request);
        call.enqueue(new Callback<ChatMessage>() {
            @Override
            public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Chatbot response failed");
                }
            }

            @Override
            public void onFailure(Call<ChatMessage> call, Throwable t) {
                callback.onError(t == null ? "Chatbot error" : t.getMessage());
            }
        });
    }
}