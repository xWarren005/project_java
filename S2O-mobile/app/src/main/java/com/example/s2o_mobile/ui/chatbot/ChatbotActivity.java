package com.example.s2o_mobile.ui.chatbot;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;

public class ChatbotActivity extends AppCompatActivity {

    protected RecyclerView recycler;
    protected EditText edtMessage;
    protected Button btnSend;
    protected ProgressBar progress;
    protected TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        bindViews();
        setupActions();
    }

    protected void bindViews() {
        recycler = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        progress = findViewById(R.id.progress);
        txtError = findViewById(R.id.txtError);
    }

    protected void setupActions() {
        btnSend.setOnClickListener(v -> sendNow());

        edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (actionId == EditorInfo.IME_ACTION_SEND || isEnter) {
                sendNow();
                return true;
            }
            return false;
        });
    }

    protected void sendNow() {

    }
}
