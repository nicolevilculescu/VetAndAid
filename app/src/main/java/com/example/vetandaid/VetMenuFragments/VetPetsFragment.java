package com.example.vetandaid.VetMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.Constants;
import com.example.vetandaid.PetProfile;
import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.VetPetsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class VetPetsFragment extends Fragment implements VetPetsAdapter.RecyclerViewClickListener, SearchView.OnQueryTextListener,
        MenuItem.OnActionExpandListener {

    RecyclerView recyclerView;

    View v;

    private VetPetsAdapter adapter;

    private ArrayList<String> idList;
    private ArrayList<Map<String, String>> list;

    VetPetsFragment ceva;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_recycler_view, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        idList = new ArrayList<>();
        list = new ArrayList<>();

        ceva = this;

        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("VetSchedule").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (!idList.contains(Objects.requireNonNull(dataSnapshot.child("petId").getValue()).toString().trim())) {
                        idList.add(Objects.requireNonNull(dataSnapshot.child("petId").getValue()).toString().trim());
                    }
                }

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Pets");
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (String petId: idList) {
                                if (petId.equals(Objects.requireNonNull(dataSnapshot.getKey()))) {
                                    Map<String, String> map;
                                    map = (Map<String, String>) dataSnapshot.getValue();
                                    assert map != null;
                                    map.put("petId", dataSnapshot.getKey());
                                    list.add(map);
                                }
                            }
                        }
                        adapter = new VetPetsAdapter(list, ceva);

                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return v;
    }

    @Override
    public void onViewClick(int position) {
        String id = list.get(position).get("petId");

        SharedPreferences settings = requireActivity().getSharedPreferences(Constants.PREFS_PET_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", id);
        editor.apply();

        settings = requireActivity().getSharedPreferences(Constants.PREFS_ACC_TYPE, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString("accType", "vet");
        editor.apply();

        startActivity(new Intent(getActivity(), PetProfile.class));
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
