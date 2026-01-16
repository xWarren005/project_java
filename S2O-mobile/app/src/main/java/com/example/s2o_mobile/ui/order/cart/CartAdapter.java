package com.example.s2o_mobile.ui.order.cart;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.data.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface ItemAction {
        void onAction(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final ItemAction onIncrease;
    private final ItemAction onDecrease;
    private final ItemAction onRemove;

    public CartAdapter(List<CartItem> initial,
                       ItemAction onIncrease,
                       ItemAction onDecrease,
                       ItemAction onRemove) {
        if (initial != null) items.addAll(initial);
        this.onIncrease = onIncrease;
        this.onDecrease = onDecrease;
        this.onRemove = onRemove;
    }

    public void setData(List<CartItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        int padding = (int) (8 * parent.getContext().getResources().getDisplayMetrics().density);
        tv.setPadding(padding, padding, padding, padding);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.textView.setText(item == null ? "Item" : safe(item.getName()));
        holder.textView.setOnClickListener(v -> {
            if (onIncrease != null) onIncrease.onAction(item);
        });
        holder.textView.setOnLongClickListener(v -> {
            if (onRemove != null) onRemove.onAction(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            this.textView = itemView;
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}