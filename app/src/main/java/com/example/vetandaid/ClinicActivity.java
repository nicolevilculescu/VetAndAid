package com.example.vetandaid;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vetandaid.ClientMenuFragments.ClinicsFragment;
import com.example.vetandaid.RecyclerViews.ScheduleRecView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ClinicActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_DAY = "extraDay";

    private DatabaseReference reference;

    private TextView clinicName, docName, addressView;
    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;

    String id, address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);

        SharedPreferences settings = getSharedPreferences(ClinicsFragment.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
        id = settings.getString("id", "default");

        clinicName = findViewById(R.id.clinicTextView);
        docName = findViewById(R.id.docNameTextView);
        addressView = findViewById(R.id.addressTextView);

        Button schedule = findViewById(R.id.schedule);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        setInfo();

        schedule.setOnClickListener(v -> {
            SharedPreferences setting = getSharedPreferences(Constants.PREFS_DATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = setting.edit();
            editor.putString("date", "after");
            editor.apply();

            openDialog();
        });

        Button chat = findViewById(R.id.chatButon);

        chat.setOnClickListener(v -> {
            SharedPreferences setting = getSharedPreferences(Constants.PREFS_CLINIC_ID, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = setting.edit();
            editor.putString("id", id);
            editor.apply();

            startActivity(new Intent(ClinicActivity.this, MessageActivity.class));
        });

        mMapView = findViewById(R.id.mapView);

        initGoogleMap(savedInstanceState);
    }

    private void setInfo() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                clinicName.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("clinic_name").getValue()).toString().trim());

                docName.setText(getString(R.string.fullName,
                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("firstName").getValue()).toString().trim(),
                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("lastName").getValue()).toString().trim()));

                addressView.setText(getString(R.string.address2,
                        Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("address").getValue()).toString().trim()));
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

    public LatLng getLocationFromAddress(String address) {
        Geocoder coder = new Geocoder(this);
        List<Address> addressList;
        LatLng p1 = null;

        try {
            addressList = coder.getFromLocationName(address, 5);
            if (addressList == null) {
                return null;
            }

            Address location = addressList.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void onMapReady(@NotNull GoogleMap map) {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                address = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("address").getValue()).toString().trim();

                mMap = map;

                LatLng latLng = getLocationFromAddress(address);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                enableMyLocation();
            }
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
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