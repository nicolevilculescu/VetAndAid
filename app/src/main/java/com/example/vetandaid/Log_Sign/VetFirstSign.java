package com.example.vetandaid.Log_Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vetandaid.R;
import com.example.vetandaid.RegistrationFragments.VetFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VetFirstSign extends AppCompatActivity {

    private DatabaseReference reference;

    private EditText weekOpen, weekClose, satOpen, satClose, sunOpen, sunClose;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_first_sign);

        SharedPreferences settings = getSharedPreferences(VetFragment.PREFS_VET_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        weekOpen = findViewById(R.id.editTextTime);
        weekClose = findViewById(R.id.editTextTime2);
        satOpen = findViewById(R.id.editTextTime3);
        satClose = findViewById(R.id.editTextTime4);
        sunOpen = findViewById(R.id.editTextTime5);
        sunClose = findViewById(R.id.editTextTime6);

        FloatingActionButton done = findViewById(R.id.done);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        done.setOnClickListener(v -> {
            if (validate()) {
                insertData();
            }
        });
    }

    private boolean validate() {
        if (!weekOpen.getText().toString().isEmpty() && !weekClose.getText().toString().isEmpty() && !satOpen.getText().toString().isEmpty()
                && !satClose.getText().toString().isEmpty() && !sunOpen.getText().toString().isEmpty() && !sunClose.getText().toString().isEmpty()) {
            if (weekOpen.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")
                    && weekClose.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")
                    && satOpen.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")
                    && satClose.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")
                    && sunOpen.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")
                    && sunClose.getText().toString().matches("(([01][0-9])|(2[0-4])):[0-5][0-9]")) {
                return true;
            } else {
                Toast.makeText(this, "Incorrect time format!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void insertData() {
        reference.child("openingHourWeek").setValue(weekOpen.getText().toString().trim());
        reference.child("closingHourWeek").setValue(weekClose.getText().toString().trim());
        reference.child("openingHourSaturday").setValue(satOpen.getText().toString().trim());
        reference.child("closingHourSaturday").setValue(satClose.getText().toString().trim());
        reference.child("openingHourSunday").setValue(sunOpen.getText().toString().trim());
        reference.child("closingHourSunday").setValue(sunClose.getText().toString().trim());
    }
}