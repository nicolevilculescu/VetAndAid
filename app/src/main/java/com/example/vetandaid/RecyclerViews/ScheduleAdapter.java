    package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.example.vetandaid.model.Schedule;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    private final String[] mHours;

    public ScheduleAdapter(String[] hours, RecyclerViewClickListener listener) {
        mHours = hours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_layout, parent, false);
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.hour.setText(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView hour;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            hour = itemView.findViewById(R.id.hour);
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
