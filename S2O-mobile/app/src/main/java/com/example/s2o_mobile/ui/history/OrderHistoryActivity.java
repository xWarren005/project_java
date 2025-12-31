package com.example.s2o_mobile.ui.history;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Order;
import com.example.s2o_mobile.data.repository.OrderRepository;
import com.example.s2o_mobile.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvEmpty;
    private RecyclerView recyclerView;

    private OrdersAdapter adapter;

    private OrderRepository orderRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>(new ArrayList<>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);

        bindViews();
        setupList();
        setupRepository();
        observeData();

        loadOrders();
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupList() {
        adapter = new OrdersAdapter(new ArrayList<>(), order -> {
            Toast.makeText(
                    this,
                    "Order #" + order.getId() + " - Tổng: " + formatMoney(order.getTotalAmount()),
                    Toast.LENGTH_SHORT
            ).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupRepository() {
        SessionManager sessionManager = new SessionManager(getApplication());
        orderRepository = OrderRepository.getInstance(sessionManager);
    }

    private void observeData() {
        loading.observe(this, isLoading -> {
            boolean show = Boolean.TRUE.equals(isLoading);
            if (progressBar != null) progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        });

        errorMessage.observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        orders.observe(this, list -> {
            List<Order> data = (list == null) ? new ArrayList<>() : list;
            adapter.setData(data);

            boolean empty = data.isEmpty();
            if (tvEmpty != null) tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void loadOrders() {
        errorMessage.setValue(null);
        orderRepository.getMyOrders(orders, errorMessage, loading);
    }

    private String formatMoney(double value) {
        return String.format(Locale.getDefault(), "%,.0f", value) + " đ";
    }

    private static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {

        interface OnItemClick {
            void onClick(Order order);
        }

        private List<Order> data;
        private final OnItemClick onItemClick;

        OrdersAdapter(List<Order> data, OnItemClick onItemClick) {
            this.data = data;
            this.onItemClick = onItemClick;
        }

        void setData(List<Order> newData) {
            this.data = (newData == null) ? new ArrayList<>() : newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Order o = data.get(position);

            holder.title.setText("Đơn #" + o.getId() + " - Bàn " + o.getTableId());

            String subtitle = "Tổng: " + holder.formatMoney(o.getTotalAmount())
                    + " | " + (o.getStatus() == null ? "" : o.getStatus());
            holder.subTitle.setText(subtitle);

            holder.itemView.setOnClickListener(v -> {
                if (onItemClick != null) onItemClick.onClick(o);
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView title;
            TextView subTitle;

            VH(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                subTitle = itemView.findViewById(android.R.id.text2);
            }

            String formatMoney(double value) {
                return String.format(Locale.getDefault(), "%,.0f", value) + " đ";
            }
        }
    }
}
