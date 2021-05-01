package com.example.vetandaid.RecyclerViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.ClientProfile;
import com.example.vetandaid.R;
import com.example.vetandaid.model.Pet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PetsAdapter extends FirebaseRecyclerAdapter<Pet, PetsAdapter.MyViewHolder> {

    private final RecyclerViewClickListener listener;
    private int count = 0, total = 0;
    private final Context context;

    private final FirebaseUser firebaseUser;

    public static final String PREFS_PET_COUNT = "PetCountPrefsFile";

    public PetsAdapter(@NonNull FirebaseRecyclerOptions<Pet> options, RecyclerViewClickListener listener, Context contex) {
        super(options);
        this.listener = listener;
        this.context = contex;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("Pets");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    assert firebaseUser != null;
                    if (firebaseUser.getUid().equals(Objects.requireNonNull(dataSnapshot.child("ownerId").getValue()).toString())) {
                        total++;
                    }
                }
                SharedPreferences settings = context.getSharedPreferences(PREFS_PET_COUNT, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("total", total);
                editor.apply();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Pet model) {
        if (position < total - 1) {
            Picasso.with(holder.img.getContext()).load(model.getUrl()).into(holder.img);
            holder.name.setText(model.getName());
        } else {
            holder.add.setText(holder.itemView.getContext().getString(R.string.add_new, "pet"));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (count >= total - 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        }
        count++;
        return new MyViewHolder(view, listener, total);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView name, add;
        RecyclerViewClickListener recyclerViewClickListener;
        int total;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener recyclerViewClickListener, int total) {
            super(itemView);
            img = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.title);
            add = itemView.findViewById(R.id.addText);
            this.recyclerViewClickListener = recyclerViewClickListener;
            this.total = total;

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
