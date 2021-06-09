package com.example.vetandaid.ClientMenuFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.R;
import com.example.vetandaid.RecyclerViews.ClientScheduleAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClientScheduleFragment extends Fragment implements ClientScheduleAdapter.RecyclerViewClickListener {

    RecyclerView recyclerView;

    private ArrayList<Map<String, String>> list;

    private TextView no;

    View v;
    SimpleDateFormat sdf;

    private ClientScheduleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_recycler_view_search, container, false);

        recyclerView = v.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        no = v.findViewById(R.id.noAppointments);

        list = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);

        ClientScheduleFragment ceva = this;

        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ClientSchedule").child(firebaseUser.getUid());
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

                    no.setText(getString(R.string.noAppointments2));
                }

                adapter = new ClientScheduleAdapter(list, ceva);

                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        EditText editText = v.findViewById(R.id.search_bar);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return v;
    }

    @Override
    public void onViewClick(int position) {

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

    private void filter(String text) {
        ArrayList<Map<String, String>> filteredList = new ArrayList<>();

        for (Map<String, String> item : list) {
            if (Objects.requireNonNull(item.get("date")).toLowerCase().contains(text.toLowerCase()) ||
                    Objects.requireNonNull(item.get("hour")).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }
}
