package com.example.s2o_mobile.data.source.remote;

import com.example.s2o_mobile.data.model.ChatMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatbotApi {

    @POST("chatbot/message")
    Call<ChatMessage> sendMessage(@Body String message);

    @GET("chatbot/conversation")
    Call<List<ChatMessage>> getConversation(@Query("userId") String userId);
}
