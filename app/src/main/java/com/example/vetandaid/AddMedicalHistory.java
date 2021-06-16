package com.example.vetandaid;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetandaid.Popups.DatePickerFragment;
import com.example.vetandaid.model.MedicalHistory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class AddMedicalHistory extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText problem, treatment, description;
    private Button date;

    private DatabaseReference databaseReference;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_history);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_PET_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        problem = findViewById(R.id.problemEditText);
        date = findViewById(R.id.datePickerButton);
        treatment = findViewById(R.id.treatmentEditText);
        description = findViewById(R.id.descriptionEditText);

        FloatingActionButton button = findViewById(R.id.addHistory);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("MedicalHistory").child(id);

        date.setOnClickListener(v -> {
            SharedPreferences setting = getSharedPreferences(Constants.PREFS_DATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = setting.edit();
            editor.putString("date", "before");
            editor.apply();

            openDialog();
        });

        button.setOnClickListener(v -> {
            if (validate()) {
                uploadInfo();
                startActivity(new Intent(AddMedicalHistory.this, PetProfile.class));
            }
        });
    }

    private void openDialog() {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getSupportFragmentManager(), "calendar dialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));
    }

    private boolean validate() {
        if (problem.getText().toString().length() != 0) {
            if (!date.getText().toString().equals("Pick date")) {
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
                Toast.makeText(this, "Date not chosen!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Problem field empty!", Toast.LENGTH_SHORT).show();
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