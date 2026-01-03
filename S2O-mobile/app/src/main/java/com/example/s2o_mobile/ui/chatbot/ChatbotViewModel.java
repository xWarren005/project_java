package com.example.s2o_mobile.ui.chatbot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public void initIfEmpty() {
        List<ChatMessage> cur = messages.getValue();
        if (cur == null || cur.isEmpty()) {
            List<ChatMessage> init = new ArrayList<>();
            init.add(new ChatMessage(ChatMessage.Sender.BOT,
                    "Xin chao, minh co the giup gi cho ban? (goi y: menu, dat ban, thanh toan, voucher)",
                    System.currentTimeMillis()));
            messages.setValue(init);
        }
    }

    private void append(ChatMessage m) {
        List<ChatMessage> cur = messages.getValue();
        if (cur == null) cur = new ArrayList<>();
        List<ChatMessage> next = new ArrayList<>(cur);
        next.add(m);
        messages.setValue(next);
    }

}
