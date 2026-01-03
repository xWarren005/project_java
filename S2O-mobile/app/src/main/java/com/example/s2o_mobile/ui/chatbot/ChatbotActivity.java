package com.example.s2o_mobile.ui.chatbot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private EditText edtMessage;
    private Button btnSend;
    private ProgressBar progress;
    private TextView txtError;

    private ChatbotViewModel viewModel;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        bindViews();
        setupRecycler();
        setupViewModel();
        observe();

        viewModel.initIfEmpty();
    }

    private void bindViews() {
        recycler = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        progress = findViewById(R.id.progress);
        txtError = findViewById(R.id.txtError);

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

    private void setupRecycler() {
        adapter = new ChatAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);
    }

    private void observe() {
        viewModel.getLoading().observe(this, loading -> {
            boolean isLoading = loading != null && loading;
            progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, err -> {
            if (!TextUtils.isEmpty(err)) {
                txtError.setText(err);
                txtError.setVisibility(View.VISIBLE);
            } else {
                txtError.setText("");
                txtError.setVisibility(View.GONE);
            }
        });

        viewModel.getMessages().observe(this, list -> {
            List<ChatbotViewModel.ChatMessage> data = list == null ? new ArrayList<>() : list;
            adapter.setData(data);
            if (!data.isEmpty()) recycler.scrollToPosition(data.size() - 1);
        });
    }

    private void sendNow() {
        String text = edtMessage.getText() == null ? "" : edtMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        edtMessage.setText("");
        viewModel.sendUserMessage(text);
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

        private final List<ChatbotViewModel.ChatMessage> data = new ArrayList<>();
        private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        void setData(List<ChatbotViewModel.ChatMessage> list) {
            data.clear();
            if (list != null) data.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int position) {
            ChatbotViewModel.ChatMessage m = data.get(position);
            String time = timeFmt.format(new Date(m.timeMillis));

            String prefix = (m.sender == ChatbotViewModel.ChatMessage.Sender.USER) ? "Ban" : "Bot";
            h.txt.setText(prefix + " (" + time + "): " + (m.text == null ? "" : m.text));

            if (m.sender == ChatbotViewModel.ChatMessage.Sender.USER) {
                h.txt.setGravity(Gravity.END);
            } else {
                h.txt.setGravity(Gravity.START);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView txt;
            VH(View itemView) {
                super(itemView);
                txt = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
