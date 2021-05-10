package com.example.vetandaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.vetandaid.RecyclerViews.ScheduleRecView;

public class ConfirmAppointment extends AppCompatActivity {

    String day, hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        Intent intent = getIntent();
        day = intent.getStringExtra(ScheduleRecView.EXTRA_DAY);
        hour = intent.getStringExtra(ScheduleRecView.EXTRA_HOUR);
    }
}