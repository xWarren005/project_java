package com.example.s2o_mobile.ui.chatbot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatbotViewModel extends ViewModel {

    public static class ChatMessage {
        public enum Sender { USER, BOT }

        public final Sender sender;
        public final String text;
        public final long timeMillis;

        public ChatMessage(Sender sender, String text, long timeMillis) {
            this.sender = sender;
            this.text = text;
            this.timeMillis = timeMillis;
        }
    }

    private final MutableLiveData<List<ChatMessage>> messages =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }
}
