package com.example.s2o_mobile.ui.order.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.R;
import com.example.s2o_mobile.data.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<Order.OrderItem> items = new ArrayList<>();
    private final DecimalFormat priceFormat = new DecimalFormat("#,###");

    public OrderItemAdapter(List<Order.OrderItem> data) {
        if (data != null) items.addAll(data);
    }

    public void setData(List<Order.OrderItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order.OrderItem item = items.get(position);
        if (item == null) {
            holder.txtName.setText("Item");
            holder.txtQty.setText("x0");
            holder.txtPrice.setText("0 VND");
            return;
        }

        holder.txtName.setText(safe(item.getName()));
        holder.txtQty.setText("x" + item.getQuantity());
        holder.txtPrice.setText(priceFormat.format(item.getSubtotal()) + " VND");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtName;
        final TextView txtQty;
        final TextView txtPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemName);
            txtQty = itemView.findViewById(R.id.txtItemQty);
            txtPrice = itemView.findViewById(R.id.txtItemPrice);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
