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

    public void sendUserMessage(String text) {
        String msg = text == null ? "" : text.trim();
        if (msg.isEmpty()) return;

        loading.setValue(true);
        error.setValue(null);

        append(new ChatMessage(ChatMessage.Sender.USER, msg, System.currentTimeMillis()));
        append(new ChatMessage(ChatMessage.Sender.BOT, generateReply(msg), System.currentTimeMillis()));

        loading.setValue(false);
    }

    private void append(ChatMessage m) {
        List<ChatMessage> cur = messages.getValue();
        if (cur == null) cur = new ArrayList<>();
        List<ChatMessage> next = new ArrayList<>(cur);
        next.add(m);
        messages.setValue(next);
    }

    private String generateReply(String userText) {
        String s = userText.toLowerCase(Locale.getDefault()).trim();

        if (containsAny(s, "menu", "mon", "do an", "thuc don")) {
            return "Ban muon xem menu cua nha hang nao? Neu da quet QR ban/restaurant thi minh co the huong dan vao man Menu.";
        }

        if (containsAny(s, "dat ban", "booking", "reservation")) {
            return "De dat ban: chon nha hang -> chon ngay gio -> so luong khach -> xac nhan. Ban muon dat luc may gio?";
        }

        if (containsAny(s, "thanh toan", "pay", "qr", "hoa don")) {
            return "Thanh toan: vao Cart -> Checkout -> chon phuong thuc (QR/tiền mat). Ban dang gap loi o buoc nao?";
        }

        if (containsAny(s, "voucher", "khuyen mai", "giam gia")) {
            return "Voucher: vao Profile/Voucher, chon voucher phu hop va ap dung khi checkout. Ban muon voucher theo nha hang hay theo don hang?";
        }

        if (containsAny(s, "hello", "hi", "xin chao", "chao")) {
            return "Chao ban! Ban can ho tro gi ve dat mon, dat ban hay thanh toan?";
        }

        return "Minh da nhan: \"" + userText + "\". Ban co the noi ro hon (menu/dat ban/thanh toan/voucher) khong?";
    }

    private boolean containsAny(String s, String... keys) {
        if (s == null) return false;
        for (String k : keys) {
            if (k != null && !k.isEmpty() && s.contains(k)) return true;
        }
        return false;
    }
}
