package com.example.vetandaid.MenuFragments;

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

import com.example.vetandaid.ClinicActivity;
import com.example.vetandaid.PetProfile;
import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.ClinicsAdapter;
import com.example.vetandaid.RecyclerViews.PetsAdapter;
import com.example.vetandaid.model.Pet;
import com.example.vetandaid.model.Vet;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class ClinicsFragment extends Fragment implements ClinicsAdapter.RecyclerViewClickListener {

    RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    View v;

    private ClinicsAdapter adapter;

    String id;

    public static final String PREFS_CLINIC_ID = "ClinicIdPrefsFile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_recycler_view, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        Query query = reference.orderByChild("accType").equalTo("vet");

        FirebaseRecyclerOptions<Vet> options = new FirebaseRecyclerOptions.Builder<Vet>()
                .setQuery(query, Vet.class).build();

        adapter = new ClinicsAdapter(options, this);

        recyclerView.setAdapter(adapter);

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
                    if (Objects.requireNonNull(dataSnapshot.child("accType").getValue()).toString().equals("vet") &&
                            Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().equals(adapter.getItem(position).getEmail())) {

                        id = dataSnapshot.getKey();

                        SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_CLINIC_ID, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("id", id);
                        editor.apply();

                        startActivity(new Intent(getActivity(), ClinicActivity.class));
                    }
                }
            }
        });
    }
}
