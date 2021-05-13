package com.example.vetandaid.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ClientScheduleAdapter extends RecyclerView.Adapter<ClientScheduleAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;

    private ArrayList<Map<String, String>> list;

    public ClientScheduleAdapter(ArrayList<Map<String, String>> list, RecyclerViewClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.date_hour.setText(holder.itemView.getContext().getString(R.string.date_time, list.get(position).get("date"), list.get(position).get("hour")));

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(list.get(position).get("clinicId")));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), "clinic_name")) {
                        holder.clinicName.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString().trim());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Pets").child(Objects.requireNonNull(list.get(position).get("petId")));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), "name")) {
                        holder.petName.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString().trim());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.three_field_item, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<Map<String, String>> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date_hour, clinicName, petName;
        RecyclerViewClickListener recyclerViewClickListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            date_hour = itemView.findViewById(R.id.field1);
            clinicName = itemView.findViewById(R.id.field2);
            petName = itemView.findViewById(R.id.field3);
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
