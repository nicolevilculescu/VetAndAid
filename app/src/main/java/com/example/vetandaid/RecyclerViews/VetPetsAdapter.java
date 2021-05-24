package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class VetPetsAdapter extends RecyclerView.Adapter<VetPetsAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    private final ArrayList<Map<String, String>> list;

    public VetPetsAdapter(ArrayList<Map<String, String>> list, RecyclerViewClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.with(holder.img.getContext()).load(list.get(position).get("url")).into(holder.img);
        holder.name.setText(list.get(position).get("name"));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView name;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            img = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.title);
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
