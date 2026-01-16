package com.example.s2o_mobile.ui.restaurant.list;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.data.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final List<Restaurant> items = new ArrayList<>();
    private final RestaurantClickListener listener;

    public RestaurantAdapter(boolean featured, RestaurantClickListener listener) {
        this.listener = listener;
    }

    public RestaurantAdapter(List<Restaurant> initial, RestaurantClickListener listener) {
        this.listener = listener;
        if (initial != null) {
            items.addAll(initial);
        }
    }

    public void submitList(List<Restaurant> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setData(List<Restaurant> list) {
        submitList(list);
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
        Restaurant item = items.get(position);
        String name = item == null ? "" : safe(item.getName());
        holder.textView.setText(name.isEmpty() ? "Restaurant" : name);
        holder.textView.setOnClickListener(v -> {
            if (listener != null && item != null) {
                listener.onRestaurantClick(item);
            }
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