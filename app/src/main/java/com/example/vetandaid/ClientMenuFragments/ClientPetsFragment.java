package com.example.vetandaid.ClientMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.AddPet;
import com.example.vetandaid.Constants;
import com.example.vetandaid.PetProfile;
import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.PetsAdapter;
import com.example.vetandaid.model.Pet;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class ClientPetsFragment extends Fragment implements PetsAdapter.RecyclerViewClickListener {

    RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    View v;

    private PetsAdapter adapter;

    String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_recycler_view_add, container, false);

        FloatingActionButton add = v.findViewById(R.id.addNew);

        recyclerView = v.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Pets");

        Query query = reference.orderByChild("ownerId").equalTo(firebaseUser.getUid());

        FirebaseRecyclerOptions<Pet> options = new FirebaseRecyclerOptions.Builder<Pet>()
                .setQuery(query, Pet.class).build();

        adapter = new PetsAdapter(options, this);

        recyclerView.setAdapter(adapter);

        add.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), AddPet.class)));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onViewClick(int position) {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                for (DataSnapshot dataSnapshot : Objects.requireNonNull(task.getResult()).getChildren()) {
                    assert firebaseUser != null;
                    if (firebaseUser.getUid().equals(Objects.requireNonNull(dataSnapshot.child("ownerId").getValue()).toString())) {
                        if (Objects.requireNonNull(dataSnapshot.child("birthdate").getValue()).toString().equals(adapter.getItem(position).getBirthdate()) &&
                                Objects.requireNonNull(dataSnapshot.child("breed").getValue()).toString().equals(adapter.getItem(position).getBreed()) &&
                                Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString().equals(adapter.getItem(position).getName()) &&
                                Objects.requireNonNull(dataSnapshot.child("ownerId").getValue()).toString().equals(adapter.getItem(position).getOwnerId()) &&
                                Objects.requireNonNull(dataSnapshot.child("species").getValue()).toString().equals(adapter.getItem(position).getSpecies()) &&
                                Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString().equals(adapter.getItem(position).getUrl())) {

                            id = dataSnapshot.getKey();

                            SharedPreferences settings = requireActivity().getSharedPreferences(Constants.PREFS_PET_ID, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("id", id);
                            editor.apply();

                            settings = requireActivity().getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
                            editor = settings.edit();
                            editor.putString("accType", "client");
                            editor.apply();

                            startActivity(new Intent(getActivity(), PetProfile.class));
                        }
                    }
                }
            }
        });
    }
}
