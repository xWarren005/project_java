package com.example.s2o_mobile.ui.review;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.s2o_mobile.data.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> items = new ArrayList<>();
    private final ReviewActionListener listener;

    public ReviewAdapter(ReviewActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Review> list) {
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
        Review item = items.get(position);
        String text = item == null ? "Review" : safe(item.getContent());
        holder.textView.setText(text.isEmpty() ? "Review" : text);
        holder.textView.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });
        holder.textView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDelete(item);
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
