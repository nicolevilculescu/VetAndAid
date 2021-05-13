package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.example.vetandaid.model.Vet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ClinicsAdapter extends FirebaseRecyclerAdapter<Vet, ClinicsAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    public ClinicsAdapter(@NonNull FirebaseRecyclerOptions<Vet> options, RecyclerViewClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Vet model) {
        holder.clinicName.setText(model.getClinic_name());
        holder.name.setText(holder.itemView.getContext().getString(R.string.fullName, model.getFirstName(), model.getLastName()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clinic_item, parent, false);
        return new MyViewHolder(view, listener);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView clinicName, name;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            clinicName = itemView.findViewById(R.id.clinicName);
            name = itemView.findViewById(R.id.doctorName);
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
