package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;

import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    private final List<Map<String, String>> vetList;

    private final int accType;

    public ChatsAdapter(List<Map<String, String>> vetList, RecyclerViewClickListener listener, int accType) {
        this.vetList = vetList;
        this.listener = listener;
        this.accType = accType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_item, parent, false);
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name1.setText(holder.itemView.getContext().getString(R.string.fullName, vetList.get(position).get("firstName"),
                vetList.get(position).get("lastName")));
        if (accType == 0) {
            holder.name2.setText(holder.itemView.getContext().getString(R.string.clinic, vetList.get(position).get("clinic_name")));
        } else {
            holder.name2.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return vetList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name1, name2;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            name1 = itemView.findViewById(R.id.name1);
            name2 = itemView.findViewById(R.id.name2);
            this.recyclerViewClickListener = recyclerViewClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onViewClick(getAbsoluteAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onViewClick(int position);
    }
}
