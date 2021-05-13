package com.example.vetandaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vetandaid.ClientMenuFragments.PetsFragment;
import com.example.vetandaid.model.MedicalHistory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicalHistory extends AppCompatActivity {

    private FloatingActionButton button;
    private EditText problem, date, treatment, description;

    private DatabaseReference databaseReference;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_history);

        SharedPreferences settings = getSharedPreferences(PetsFragment.PREFS_PET_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        problem = findViewById(R.id.problemEditText);
        date = findViewById(R.id.dateEditText);
        treatment = findViewById(R.id.treatmentEditText);
        description = findViewById(R.id.descriptionEditText);

        button = findViewById(R.id.addHistory);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id);

        button.setOnClickListener(v -> {
            if (validate()) {
                uploadInfo();
                startActivity(new Intent(AddMedicalHistory.this, PetProfile.class));
            }
        });
    }

    private boolean validate() {
        if (problem.getText().toString().length() != 0) {
            if (date.getText().toString().length() != 0) {
                if (treatment.getText().toString().length() != 0) {
                    if (description.getText().toString().length() == 0) {
                        description.setText("-");
                    }
                    return true;
                } else {
                    Toast.makeText(this, "Treatment field empty!", Toast.LENGTH_SHORT).show();
                    treatment.requestFocus();
                    return false;
                }
            } else {
                Toast.makeText(this, "Date field empty", Toast.LENGTH_SHORT).show();
                date.requestFocus();
                return false;
            }
        } else {
            Toast.makeText(this, "Problem field empty", Toast.LENGTH_SHORT).show();
            problem.requestFocus();
            return false;
        }
    }

    private void uploadInfo() {
        MedicalHistory hist = new MedicalHistory(date.getText().toString().trim(), problem.getText().toString().trim(),
                treatment.getText().toString().trim(), description.getText().toString().trim());
        String uploadId = databaseReference.push().getKey();
        assert uploadId != null;
        databaseReference.child(uploadId).setValue(hist);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AddMedicalHistory.this, PetProfile.class));
    }
}