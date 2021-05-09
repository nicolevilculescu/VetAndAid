package com.example.vetandaid.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetandaid.ClinicActivity;
import com.example.vetandaid.MenuFragments.ClinicsFragment;
import com.example.vetandaid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ScheduleRecView extends AppCompatActivity implements ScheduleAdapter.RecyclerViewClickListener {

    String id;

    ArrayList<String> hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        Intent intent = getIntent();
        String day = intent.getStringExtra(ClinicActivity.EXTRA_DAY);

        SharedPreferences settings = getSharedPreferences(ClinicsFragment.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        try {
            calendar.setTime(Objects.requireNonNull(sdf.parse(day)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] h = getResources().getStringArray(R.array.hours);
        hours = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        SimpleDateFormat finalSdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = finalSdf.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date finalDate = date;
        reference1.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                String opening = "", closing = "";
                for (DataSnapshot dataSnapshot : Objects.requireNonNull(task.getResult()).getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), "openingHourWeek") && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                        opening = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    } else if (Objects.equals(dataSnapshot.getKey(), "closingHourWeek") && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                        closing = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    } else if (Objects.equals(dataSnapshot.getKey(), "openingHourSaturday") &&
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        opening = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    } else if (Objects.equals(dataSnapshot.getKey(), "closingHourSaturday") &&
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        closing = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    } else if (Objects.equals(dataSnapshot.getKey(), "openingHourSunday") &&
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        opening = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    } else if (Objects.equals(dataSnapshot.getKey(), "closingHourSunday") &&
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        closing = Objects.requireNonNull(dataSnapshot.getValue()).toString().trim();
                    }
                }
                for (String s: h) {
                    if (s.compareTo(opening) >= 0 && s.compareTo(closing) <= 0) {
                        hours.add(s);
                    }
                }

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("VetSchedule").child(id);

                reference2.get().addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task1.getException());
                    } else {
                        for (DataSnapshot dataSnapshot : Objects.requireNonNull(task1.getResult()).getChildren()) {
                            assert finalDate != null;
                            if (Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString().equals(finalSdf.format(finalDate))) {
                                hours.remove(Objects.requireNonNull(dataSnapshot.child("hour").getValue()).toString());
                            }
                        }
                        ScheduleAdapter adapter = new ScheduleAdapter(hours, this);

                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public void onViewClick(int position) {
        /*Intent resultIntent = new Intent();
        resultIntent.putExtra("result", adapter.getItem(position).getName());
        setResult(RESULT_OK, resultIntent);
        finish();*/
    }
}
