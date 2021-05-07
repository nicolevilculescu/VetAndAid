package com.example.vetandaid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.vetandaid.Log_Sign.VetFirstSign;
import com.example.vetandaid.MenuFragments.ClinicsFragment;
import com.example.vetandaid.RecyclerViews.Breeds;
import com.example.vetandaid.RecyclerViews.ScheduleRecView;
import com.example.vetandaid.model.Schedule;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ClinicActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_DAY = "extraDay";

    private DatabaseReference reference;

    private TextView clinicName, docName;
    private Button schedule;
    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);

        SharedPreferences settings = getSharedPreferences(ClinicsFragment.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        clinicName = findViewById(R.id.clinicTextView);
        docName = findViewById(R.id.docNameTextView);

        schedule = findViewById(R.id.schedule);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        setInfo();

        schedule.setOnClickListener(v -> {
            openDialog();
            //startActivity(new Intent(ClinicActivity.this, VetFirstSign.class));
        });

        mMapView = findViewById(R.id.mapView);

        initGoogleMap(savedInstanceState);
    }

    private void setInfo() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                clinicName.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("clinic_name").getValue()).toString().trim());

                docName.setText(getString(R.string.doctorName,
                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("firstName").getValue()).toString().trim(),
                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("lastName").getValue()).toString().trim()));
            }
        });
    }

    private void openDialog() {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getSupportFragmentManager(), "calendar dialog");
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        Intent intent = new Intent(ClinicActivity.this, ScheduleRecView.class);
        intent.putExtra(EXTRA_DAY, currentDate);
        startActivityForResult(intent, 1);
    }
}