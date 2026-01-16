package com.example.s2o_mobile.ui.menu;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.data.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(MenuItem item);
    }

    private final List<MenuItem> items = new ArrayList<>();
    private final OnItemClick onItemClick;

    public MenuAdapter(List<MenuItem> initial, OnItemClick onItemClick) {
        if (initial != null) items.addAll(initial);
        this.onItemClick = onItemClick;
    }

    public void setData(List<MenuItem> list) {
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
        MenuItem item = items.get(position);
        holder.textView.setText(item == null ? "Item" : safe(item.getName()));
        holder.textView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(item);
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