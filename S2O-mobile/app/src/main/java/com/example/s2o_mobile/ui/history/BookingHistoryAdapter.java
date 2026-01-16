package com.example.s2o_mobile.ui.history;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.data.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private final List<Booking> items = new ArrayList<>();

    public void submitList(List<Booking> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
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
        Booking item = items.get(position);
        holder.textView.setText(item == null ? "Booking" : String.valueOf(item.getId()));
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
