package com.example.vetandaid.VetMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.Constants;
import com.example.vetandaid.PetProfile;
import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.VetScheduleAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class VetScheduleFragment extends Fragment implements VetScheduleAdapter.RecyclerViewClickListener, SearchView.OnQueryTextListener,
        MenuItem.OnActionExpandListener {

    RecyclerView recyclerView;

    private ArrayList<Map<String, String>> list;

    View v;
    SimpleDateFormat sdf;

    private VetScheduleAdapter adapter;

    private TextView no;

    VetScheduleFragment ceva;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_recycler_view, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        no = v.findViewById(R.id.noChats);

        list = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);

        ceva = this;

        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("VetSchedule").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    try {
                        Date date ;
                        date = sdf.parse(Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString().trim());
                        assert date != null;
                        cal.setTime(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (cal.compareTo(calendar) >= 0) {
                        Map<String, String> map;
                        map = (Map<String, String>) dataSnapshot.getValue();
                        list.add(map);
                    }
                }
                try {
                    sort(list);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (list.size() != 0) {
                    no.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    no.setVisibility(View.VISIBLE);

                    no.setText(getString(R.string.noAppointments));
                }

                adapter = new VetScheduleAdapter(list, ceva);

                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        setHasOptionsMenu(true);

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

    public void sort(ArrayList<Map<String, String>> list) throws ParseException {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                Date date1, date2 ;
                date1 = sdf.parse(Objects.requireNonNull(list.get(j).get("date")));
                date2 = sdf.parse(Objects.requireNonNull(list.get(j + 1).get("date")));
                assert date1 != null;
                if (date1.compareTo(date2) > 0) {
                    Map<String, String> map = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, map);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        adapter.setFilter(list);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            ArrayList<Map<String, String>> filteredList = new ArrayList<>(list);
            adapter.setFilter(filteredList);
            return false;
        }
        newText = newText.toLowerCase();
        final ArrayList<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> item : list) {
            if (Objects.requireNonNull(item.get("date")).toLowerCase().contains(newText.toLowerCase()) ||
                    Objects.requireNonNull(item.get("hour")).toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setFilter(filteredList);
        return true;
    }
}
