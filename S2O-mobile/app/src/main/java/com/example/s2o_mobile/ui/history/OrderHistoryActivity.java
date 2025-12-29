package com.example.s2o_mobile.ui.history;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private OrdersAdapter adapter;
    private OrderRepository orderRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>(new ArrayList<>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        adapter = new OrdersAdapter(new ArrayList<>(), order ->
                Toast.makeText(this, "Order #" + order.getId(), Toast.LENGTH_SHORT).show()
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(getApplication());
        orderRepository = OrderRepository.getInstance(sessionManager);

        loading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        errorMessage.observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        orders.observe(this, list -> {
            adapter.setData(list);
            boolean empty = list.size() == 0;
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        orderRepository.getMyOrders(orders, errorMessage, loading);
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
            data = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Order order = data.get(position);
            holder.title.setText("Order #" + order.getId());
            holder.itemView.setOnClickListener(v -> onItemClick.onClick(order));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView title;

            VH(@NonNull android.view.View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
