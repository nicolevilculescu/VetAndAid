package com.example.vetandaid.Log_Sign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vetandaid.R;
import com.example.vetandaid.RegistrationFragments.VetFragment;
import com.example.vetandaid.TimePickerFragment;
import com.example.vetandaid.VetProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class VetFirstSign extends AppCompatActivity implements TimePickerFragment.TimePickedListener {

    private DatabaseReference reference;

    //private EditText weekOpen, weekClose, satOpen, satClose, sunOpen, sunClose;
    private Button weekOpen, weekClose, satOpen, satClose, sunOpen, sunClose;

    String id, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_first_sign);

        SharedPreferences settings = getSharedPreferences(VetFragment.PREFS_VET_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        weekOpen = findViewById(R.id.buttonTime1);
        weekClose = findViewById(R.id.buttonTime2);
        satOpen = findViewById(R.id.buttonTime3);
        satClose = findViewById(R.id.buttonTime4);
        sunOpen = findViewById(R.id.buttonTime5);
        sunClose = findViewById(R.id.buttonTime6);

        FloatingActionButton done = findViewById(R.id.done);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        weekOpen.setOnClickListener(v -> {
            clickHours(weekOpen);
        });

        weekClose.setOnClickListener(v -> {
            clickHours(weekClose);
        });

        satOpen.setOnClickListener(v -> {
            clickHours(satOpen);
        });

        satClose.setOnClickListener(v -> {
            clickHours(satClose);
        });

        sunOpen.setOnClickListener(v -> {
            clickHours(sunOpen);
        });

        sunClose.setOnClickListener(v -> {
            clickHours(sunClose);
        });

        done.setOnClickListener(v -> {
            if (validate()) {
                insertData();
                startActivity(new Intent(VetFirstSign.this, VetProfile.class));
            }
        });
    }

    private void clickHours(Button button) {
        DialogFragment timePicker = TimePickerFragment.newInstance(button.getId());
        timePicker.show(getSupportFragmentManager(), String.valueOf(button));
    }

    private boolean validate() {
        if (!weekOpen.getText().toString().isEmpty() && !weekClose.getText().toString().isEmpty() && !satOpen.getText().toString().isEmpty()
                && !satClose.getText().toString().isEmpty() && !sunOpen.getText().toString().isEmpty() && !sunClose.getText().toString().isEmpty()) {
            return true;
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

    @Override
    public void onTimePicked(Calendar time, int id) {
        Button button = findViewById(id);
        String hour, minute;

        hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY));
        minute = String.valueOf(time.get(Calendar.MINUTE));

        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        button.setText(getString(R.string.time, hour, minute));
    }
}