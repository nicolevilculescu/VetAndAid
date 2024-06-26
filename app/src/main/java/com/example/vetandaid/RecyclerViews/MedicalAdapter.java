package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.example.vetandaid.model.MedicalHistory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MedicalAdapter extends FirebaseRecyclerAdapter<MedicalHistory, MedicalAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    public MedicalAdapter(@NonNull FirebaseRecyclerOptions<MedicalHistory> options, RecyclerViewClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MedicalHistory model) {
        holder.problem.setText(holder.itemView.getContext().getString(R.string.problem, model.getProblem()));
        holder.date.setText(holder.itemView.getContext().getString(R.string.period, model.getDate()));
        holder.treatment.setText(holder.itemView.getContext().getString(R.string.treatment, model.getTreatment()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.three_field_item, parent, false), listener);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView problem, date, treatment;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            problem = itemView.findViewById(R.id.field1);
            date = itemView.findViewById(R.id.field2);
            treatment = itemView.findViewById(R.id.field3);
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