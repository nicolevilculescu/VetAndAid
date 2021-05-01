package com.example.vetandaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vetandaid.Log_Sign.MainActivity;
import com.example.vetandaid.MenuFragments.ChatFragment;
import com.example.vetandaid.MenuFragments.ClinicsFragment;
import com.example.vetandaid.MenuFragments.PetsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ClientProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        name = headerView.findViewById(R.id.profileName);
        email = headerView.findViewById(R.id.profileEmail);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (firebaseUser.getUid().equals(dataSnapshot.getKey())) {
                        name.setText(getString(R.string.display_name, Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child("lastName").getValue()).toString()));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        assert firebaseUser != null;
        email.setText(firebaseUser.getEmail());

        drawer = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PetsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_pets);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_pets:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PetsFragment()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                break;
            case R.id.nav_clinics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ClinicsFragment()).commit();

                /*SharedPreferences setting = getSharedPreferences(Constants.CLINICS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                editor.putString("from", "ClientProfile");
                editor.apply();*/

                break;
            case R.id.nav_logout:
                //logging out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ClientProfile.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}