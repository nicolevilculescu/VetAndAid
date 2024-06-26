package com.example.vetandaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetandaid.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MedicalHistoryActivity extends AppCompatActivity {

    private TextView problem, description, date, treatment;
    private Button delete;

    String id1, id2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_PET_ID, Context.MODE_PRIVATE);
        id1 = settings.getString("id", "default");

        settings = getSharedPreferences(PetProfile.PREFS_MEDICAL_ID, Context.MODE_PRIVATE);
        id2 = settings.getString("id", "default");

        problem = findViewById(R.id.problemTextView);
        description = findViewById(R.id.descriptionTextView);
        date = findViewById(R.id.dateTextView);
        treatment = findViewById(R.id.treatmentTextView);

        delete = findViewById(R.id.delButton);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id1).child(id2);
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                problem.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("problem").getValue()).toString().trim());
                date.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("date").getValue()).toString().trim());
                treatment.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("treatment").getValue()).toString().trim());
                description.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("description").getValue()).toString().trim());
            }
        });

        delete.setOnClickListener(v -> {
            reference.removeValue();
            startActivity(new Intent(MedicalHistoryActivity.this, PetProfile.class));
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}