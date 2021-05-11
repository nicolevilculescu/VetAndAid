package com.example.vetandaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vetandaid.MenuFragments.ClinicsFragment;
import com.example.vetandaid.RecyclerViews.ScheduleAdapter;
import com.example.vetandaid.RecyclerViews.ScheduleRecView;
import com.example.vetandaid.model.ClientSchedule;
import com.example.vetandaid.model.MedicalHistory;
import com.example.vetandaid.model.VetSchedule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ConfirmAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    private TextView date, time;

    private Button cancel, confirm;

    String day, hour, petId, clinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        SharedPreferences settings = getSharedPreferences(ClinicsFragment.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        clinicId = settings.getString("id", "default");

        Intent intent = getIntent();
        day = intent.getStringExtra(ScheduleRecView.EXTRA_DAY);
        hour = intent.getStringExtra(ScheduleRecView.EXTRA_HOUR);

        date = findViewById(R.id.dateView);
        time = findViewById(R.id.timeView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");

        Spinner spinner = findViewById(R.id.spinner);

        list = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.child("ownerId").getValue()).toString().trim().equals(firebaseUser.getUid()) &&
                            !Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString().trim().equals("no")) {
                        list.add(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        spinner.setOnItemSelectedListener(this);

        cancel = findViewById(R.id.cancelButton);
        confirm = findViewById(R.id.confirmButton);

        date.setText(day);
        time.setText(hour);

        cancel.setOnClickListener(v -> finish());

        confirm.setOnClickListener(v -> {
            uploadInfo();
        });
    }

    private void uploadInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ClientSchedule").child(firebaseUser.getUid());

        ClientSchedule clientSchedule = new ClientSchedule(hour, petId, day, clinicId);
        String uploadId = databaseReference.push().getKey();
        assert uploadId != null;
        databaseReference.child(uploadId).setValue(clientSchedule);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("VetSchedule").child(clinicId);

        VetSchedule vetSchedule = new VetSchedule(hour, petId, day, firebaseUser.getUid());
        uploadId = databaseReference.push().getKey();
        assert uploadId != null;
        databaseReference.child(uploadId).setValue(vetSchedule);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        databaseReference.get().addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e("firebase", "Error getting data", task1.getException());
            } else {
                for (DataSnapshot dataSnapshot : Objects.requireNonNull(task1.getResult()).getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString().trim().equals(selected) &&
                            Objects.requireNonNull(dataSnapshot.child("ownerId").getValue()).toString().trim().equals(firebaseUser.getUid())) {
                        petId = dataSnapshot.getKey();
                    }
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}