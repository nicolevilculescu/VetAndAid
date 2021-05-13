package com.example.vetandaid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.vetandaid.ClientMenuFragments.ChatFragment;
import com.example.vetandaid.ClientMenuFragments.PetsFragment;
import com.example.vetandaid.Log_Sign.MainActivity;
import com.example.vetandaid.VetMenuFragments.VetScheduleFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VetProfile extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    private FirebaseUser firebaseUser;

    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_profile);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        NavigationView navigationView = findViewById(R.id.nav_view2);
        View headerView = navigationView.getHeaderView(0);

        name = headerView.findViewById(R.id.profileName);
        TextView email = headerView.findViewById(R.id.profileEmail);

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

        drawer = findViewById(R.id.drawer_layout2);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                    new VetScheduleFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_schedule);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_schedule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                        new VetScheduleFragment()).commit();
                break;
            case R.id.nav_pets2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                        new PetsFragment()).commit();
                break;
            case R.id.nav_chat2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                        new ChatFragment()).commit();
                break;
            case R.id.nav_logout2:
                //logging out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(VetProfile.this, MainActivity.class);
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