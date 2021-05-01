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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MedicalAdapter extends FirebaseRecyclerAdapter<MedicalHistory, MedicalAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    private int count = 0, total = 0;

    public MedicalAdapter(@NonNull FirebaseRecyclerOptions<MedicalHistory> options, RecyclerViewClickListener listener, String id) {
        super(options);
        this.listener = listener;

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ignored : snapshot.getChildren()) {
                    total++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MedicalHistory model) {
        if (position < total - 1) {
            holder.problem.setText(holder.itemView.getContext().getString(R.string.problem, model.getProblem()));
            holder.date.setText(holder.itemView.getContext().getString(R.string.period, model.getDate()));
            holder.treatment.setText(holder.itemView.getContext().getString(R.string.treatment, model.getTreatment()));
        } else {
            holder.add.setText(holder.itemView.getContext().getString(R.string.add_new, "medical record"));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (count >= total - 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_item, parent, false);
        }
        count++;
        return new MyViewHolder(view, listener);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView problem, date, treatment, add;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            problem = itemView.findViewById(R.id.problem);
            date = itemView.findViewById(R.id.period);
            treatment = itemView.findViewById(R.id.treatment);
            add = itemView.findViewById(R.id.addText);
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
